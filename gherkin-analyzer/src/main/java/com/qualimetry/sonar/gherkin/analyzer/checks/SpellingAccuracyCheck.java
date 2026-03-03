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

    private JLanguageTool languageTool;
    private List<String> wordsToIgnore;

    public void setWordsToIgnore(String wordsToIgnoreParam) {
        this.wordsToIgnoreParam = wordsToIgnoreParam != null ? wordsToIgnoreParam : DEFAULT_WORDS_TO_IGNORE;
        this.wordsToIgnore = null;
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        languageTool = null;
        wordsToIgnore = parseWordsToIgnore(wordsToIgnoreParam);
        try {
            Language lang = Languages.getLanguageForShortCode("en-US");
            JLanguageTool lt = new JLanguageTool(lang);
            for (org.languagetool.rules.Rule rule : lt.getAllActiveRules()) {
                if (!(rule instanceof SpellingCheckRule)) {
                    lt.disableRule(rule.getId());
                }
            }
            for (org.languagetool.rules.Rule rule : lt.getAllActiveRules()) {
                if (rule instanceof SpellingCheckRule spellingRule && wordsToIgnore != null && !wordsToIgnore.isEmpty()) {
                    spellingRule.addIgnoreTokens(wordsToIgnore);
                }
            }
            this.languageTool = lt;
        } catch (Exception e) {
            // LanguageTool failed to load; skip spell checking for this file
            languageTool = null;
        }
    }

    @Override
    public void leaveFeatureFile(FeatureFile file) {
        languageTool = null;
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        checkText(feature.name(), feature.position(), "Feature name");
        if (feature.description() != null && !feature.description().isEmpty()) {
            checkText(feature.description(), feature.position(), "Feature description");
        }
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        checkText(rule.name(), rule.position(), "Rule name");
        if (rule.description() != null && !rule.description().isEmpty()) {
            checkText(rule.description(), rule.position(), "Rule description");
        }
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        checkText(scenario.name(), scenario.position(), "Scenario name");
        if (scenario.description() != null && !scenario.description().isEmpty()) {
            checkText(scenario.description(), scenario.position(), "Scenario description");
        }
    }

    @Override
    public void visitStep(StepDefinition step) {
        checkText(step.text(), step.position(), "Step text");
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

    private void checkText(String text, TextPosition basePosition, String contextLabel) {
        if (text == null || text.isEmpty() || languageTool == null) {
            return;
        }
        List<RuleMatch> matches;
        try {
            matches = languageTool.check(text);
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
            int line = basePosition.line() + lineOffset;
            int column = lineOffset == 0
                    ? basePosition.column() + fromPos
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
