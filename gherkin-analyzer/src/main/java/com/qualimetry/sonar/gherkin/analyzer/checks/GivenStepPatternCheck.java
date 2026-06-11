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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.BackgroundDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Checks that <em>Given</em> steps match a configurable regular-expression
 * pattern. And/But steps continuing a Given are checked as well.
 * <p>
 * This rule allows teams to enforce naming conventions on precondition steps,
 * such as requiring them to start with a specific phrase or follow a
 * particular sentence structure.
 */
@Rule(key = "given-step-pattern")
public class GivenStepPatternCheck extends BaseCheck {

    private static final String DEFAULT_PATTERN = ".*";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression that Given step text must match",
            defaultValue = DEFAULT_PATTERN)
    private String pattern = DEFAULT_PATTERN;

    private Pattern compiledPattern;
    private String effectiveKeywordType;

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = null;
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        effectiveKeywordType = null;
        if (compiledPattern == null) {
            try {
                compiledPattern = Pattern.compile(pattern);
            } catch (PatternSyntaxException e) {
                addFileIssue("Invalid regex pattern \"" + pattern + "\": " + e.getMessage());
            }
        }
    }

    @Override
    public void visitBackground(BackgroundDefinition background) {
        effectiveKeywordType = null;
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        effectiveKeywordType = null;
    }

    @Override
    public void visitStep(StepDefinition step) {
        if (!"CONJUNCTION".equals(step.keywordType())) {
            effectiveKeywordType = step.keywordType();
        }
        if (compiledPattern == null || !"CONTEXT".equals(effectiveKeywordType)) {
            return;
        }
        if (!compiledPattern.matcher(step.text()).matches()) {
            addIssue(step.position(),
                    "Given step does not match the required pattern: " + pattern);
        }
    }
}
