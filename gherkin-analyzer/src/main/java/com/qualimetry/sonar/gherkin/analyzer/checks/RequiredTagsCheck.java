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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Checks that every Scenario has at least one tag matching a configurable pattern.
 * <p>
 * This is useful for teams that require categorization tags (e.g. {@code @smoke},
 * {@code @regression}) on every scenario. Tags inherited from the enclosing
 * Feature and Rule count towards the match. The default pattern {@code .*}
 * matches everything, so the rule is effectively inactive unless configured
 * with a meaningful pattern such as {@code smoke|regression|integration}.
 */
@Rule(key = "required-tags")
public class RequiredTagsCheck extends BaseCheck {

    private static final String DEFAULT_PATTERN = ".*";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression that at least one tag must match (without the leading @)",
            defaultValue = DEFAULT_PATTERN)
    private String pattern = DEFAULT_PATTERN;

    private Pattern compiledPattern;
    private Boolean patternInvalid;
    private List<TagDefinition> featureTags = List.of();
    private List<TagDefinition> ruleTags = List.of();

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = null; // reset cached pattern
        this.patternInvalid = null;
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        patternInvalid = null;
        featureTags = List.of();
        ruleTags = List.of();
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        featureTags = feature.tags();
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        ruleTags = rule.tags();
    }

    @Override
    public void leaveRule(RuleDefinition rule) {
        ruleTags = List.of();
    }

    private Pattern getCompiledPattern() {
        if (compiledPattern != null) {
            return compiledPattern;
        }
        if (Boolean.TRUE.equals(patternInvalid)) {
            return null;
        }
        try {
            compiledPattern = Pattern.compile(pattern);
            return compiledPattern;
        } catch (PatternSyntaxException e) {
            addFileIssue("Invalid regex pattern \"" + pattern + "\": " + e.getMessage());
            patternInvalid = true;
            return null;
        }
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        Pattern p = getCompiledPattern();
        if (p == null) {
            return;
        }
        List<TagDefinition> effectiveTags = new ArrayList<>(scenario.tags());
        effectiveTags.addAll(featureTags);
        effectiveTags.addAll(ruleTags);

        boolean hasMatch = false;
        for (TagDefinition tag : effectiveTags) {
            if (p.matcher(tag.name()).matches()) {
                hasMatch = true;
                break;
            }
        }
        if (!hasMatch) {
            addIssue(scenario.position(),
                    "Add at least one tag matching the required pattern \"" + pattern + "\" to this scenario.");
        }
    }
}
