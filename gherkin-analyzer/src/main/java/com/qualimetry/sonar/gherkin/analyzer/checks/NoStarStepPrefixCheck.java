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

/**
 * Checks that the star (*) step prefix is not used.
 * <p>
 * Note: The {@code *} prefix is valid Gherkin syntax — the specification
 * explicitly documents it as a step keyword for bullet-point-style steps.
 * This rule is an opinionated style preference. Teams that want to allow
 * {@code *} steps can deactivate this rule. Only the literal keyword is
 * checked; the semantic {@code UNKNOWN} keyword type is handled separately
 * by {@code NoUnknownStepTypeCheck} to avoid overlapping issues.
 */
@Rule(key = "no-star-step-prefix")
public class NoStarStepPrefixCheck extends BaseCheck {

    @Override
    public void visitStep(StepDefinition step) {
        if (step.keyword() != null && step.keyword().trim().equals("*")) {
            addIssue(step.position(),
                    "Replace the \"*\" prefix with an explicit keyword (Given, When, Then, And, or But).");
        }
    }
}
