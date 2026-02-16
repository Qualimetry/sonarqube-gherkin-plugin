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
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;

/**
 * Template rule that flags step text, names, and descriptions matching a
 * configurable regex pattern.
 * <p>
 * More general than {@code business-language-only} — teams can ban
 * project-specific anti-patterns such as hardcoded URLs, environment names,
 * SQL fragments, or any other text pattern that should not appear in
 * Gherkin specifications.
 * <p>
 * When the pattern is empty (the default), the rule does nothing.
 */
@Rule(key = "no-restricted-patterns")
public class NoRestrictedPatternsCheck extends BaseCheck {

    private static final String DEFAULT_PATTERN = "";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression to match against step text, names, and descriptions. "
                    + "Leave empty to disable the rule.",
            defaultValue = DEFAULT_PATTERN)
    private String pattern = DEFAULT_PATTERN;

    private Pattern compiledPattern;

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = null; // reset cached pattern
    }

    private Pattern getCompiledPattern() {
        if (compiledPattern == null && !pattern.isEmpty()) {
            compiledPattern = Pattern.compile(pattern);
        }
        return compiledPattern;
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        checkText(feature.name(), feature.position(), "Feature name");
        checkText(feature.description(), feature.position(), "Feature description");
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        checkText(rule.name(), rule.position(), "Rule name");
        checkText(rule.description(), rule.position(), "Rule description");
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        checkText(scenario.name(), scenario.position(), "Scenario name");
        checkText(scenario.description(), scenario.position(), "Scenario description");
    }

    @Override
    public void visitStep(StepDefinition step) {
        checkText(step.text(), step.position(), "Step text");
    }

    private void checkText(String text, TextPosition position, String elementType) {
        if (text == null || text.isEmpty()) {
            return;
        }
        Pattern p = getCompiledPattern();
        if (p == null) {
            return;
        }
        if (p.matcher(text).find()) {
            addIssue(position,
                    elementType + " matches the restricted pattern \"" + pattern + "\". Remove or rephrase it.");
        }
    }
}
