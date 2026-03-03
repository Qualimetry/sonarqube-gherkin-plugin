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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;

/**
 * Checks that <em>When</em> steps match a configurable regular-expression
 * pattern.
 * <p>
 * This rule allows teams to enforce naming conventions on action steps,
 * such as requiring them to start with a specific phrase or follow a
 * particular sentence structure.
 */
@Rule(key = "when-step-pattern")
public class WhenStepPatternCheck extends BaseCheck {

    private static final String DEFAULT_PATTERN = ".*";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression that When step text must match",
            defaultValue = DEFAULT_PATTERN)
    private String pattern = DEFAULT_PATTERN;

    private Pattern compiledPattern;

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = null;
    }

    @Override
    public void visitStep(StepDefinition step) {
        if (!"ACTION".equals(step.keywordType())) {
            return;
        }
        if (compiledPattern == null) {
            compiledPattern = Pattern.compile(pattern);
        }
        if (!compiledPattern.matcher(step.text()).matches()) {
            addIssue(step.position(),
                    "When step does not match the required pattern: " + pattern);
        }
    }
}
