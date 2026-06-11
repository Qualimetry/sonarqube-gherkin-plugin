/*
 * Copyright 2026 SHAZAM Analytics Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qualimetry.sonar.gherkin.analyzer.checks;

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Spell-checks Gherkin feature files using LanguageTool.
 * Reports spelling mistakes in feature names and descriptions, scenario and rule
 * names and descriptions, and step text. Use the {@code wordsToIgnore} parameter
 * to exclude product names, technical terms, or domain vocabulary.
 */
@Rule(key = "spelling-accuracy")
public class SpellingAccuracyCheck extends BaseCheck {

    private static final String DEFAULT_WORDS_TO_IGNORE = "";

    @RuleProperty(
            key = "wordsToIgnore",
            description = "Comma-separated list of words to exclude from spell checking (e.g. product names, technical terms).",
            defaultValue = DEFAULT_WORDS_TO_IGNORE)
    private String wordsToIgnoreParam = DEFAULT_WORDS_TO_IGNORE;

    /**
     * JLanguageTool construction takes seconds; instances are cached per
     * ignore-word configuration and shared across files and check instances.
     * {@link JLanguageTool#check} is not thread-safe, so callers synchronize
     * on the instance.
     */
    private static final Map<String, JLanguageTool> TOOL_CACHE = new ConcurrentHashMap<>();

    private JLanguageTool languageTool;

    public void setWordsToIgnore(String wordsToIgnoreParam) {
        this.wordsToIgnoreParam = wordsToIgnoreParam != null ? wordsToIgnoreParam : DEFAULT_WORDS_TO_IGNORE;
        this.languageTool = null;
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        if (languageTool != null) {
            return;
        }
        List<String> wordsToIgnore = parseWordsToIgnore(wordsToIgnoreParam);
        try {
            languageTool = TOOL_CACHE.computeIfAbsent(
                    String.join(",", wordsToIgnore),
                    key -> buildLanguageTool(wordsToIgnore));
        } catch (Exception e) {
            // LanguageTool failed to load; skip spell checking for this file
            languageTool = null;
        }
    }

    private static JLanguageTool buildLanguageTool(List<String> wordsToIgnore) {
        Language lang = Languages.getLanguageForShortCode("en-US");
        JLanguageTool lt = new JLanguageTool(lang);
        for (org.languagetool.rules.Rule rule : lt.getAllActiveRules()) {
            if (!(rule instanceof SpellingCheckRule)) {
                lt.disableRule(rule.getId());
            }
        }
        if (!wordsToIgnore.isEmpty()) {
            for (org.languagetool.rules.Rule rule : lt.getAllActiveRules()) {
                if (rule instanceof SpellingCheckRule spellingRule) {
                    spellingRule.addIgnoreTokens(wordsToIgnore);
                }
            }
        }
        return lt;
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        checkText(feature.name(), feature.position());
        checkDescription(feature.description(), feature.position());
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        checkText(rule.name(), rule.position());
        checkDescription(rule.description(), rule.position());
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        checkText(scenario.name(), scenario.position());
        checkDescription(scenario.description(), scenario.position());
    }

    @Override
    public void visitStep(StepDefinition step) {
        checkText(step.text(), step.position());
    }

    private List<String> parseWordsToIgnore(String param) {
        if (param == null || param.isBlank()) {
            return List.of();
        }
        return Arrays.stream(param.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private void checkText(String text, TextPosition basePosition) {
        reportMatches(text, basePosition.line(), basePosition.column());
    }

    /**
     * Descriptions begin on the line after the keyword line that carries the
     * element's position, so matches are offset by one extra line.
     */
    private void checkDescription(String text, TextPosition keywordPosition) {
        reportMatches(text, keywordPosition.line() + 1, 1);
    }

    private void reportMatches(String text, int startLine, int startColumn) {
        if (text == null || text.isEmpty() || languageTool == null) {
            return;
        }
        List<RuleMatch> matches;
        try {
            synchronized (languageTool) {
                matches = languageTool.check(text);
            }
        } catch (IOException e) {
            return;
        }
        for (RuleMatch match : matches) {
            int fromPos = match.getFromPos();
            int lineOffset = 0;
            int lastNewline = -1;
            for (int i = 0; i < fromPos && i < text.length(); i++) {
                if (text.charAt(i) == '\n') {
                    lineOffset++;
                    lastNewline = i;
                }
            }
            int line = startLine + lineOffset;
            int column = lineOffset == 0
                    ? startColumn + fromPos
                    : (fromPos - lastNewline);
            if (column < 1) {
                column = 1;
            }
            TextPosition position = new TextPosition(line, column);
            String message = match.getMessage();
            if (match.getSuggestedReplacements() != null && !match.getSuggestedReplacements().isEmpty()) {
                message = message + " Consider: " + String.join(", ", match.getSuggestedReplacements());
            }
            addIssue(position, message);
        }
    }
}
