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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.regex.Pattern;

/**
 * Checks that Scenario Outlines reference at least one {@code <placeholder>} variable.
 * <p>
 * A Scenario Outline that never uses placeholders in its step text, name, or description
 * is functionally identical to running the same scenario N times with identical inputs,
 * which is almost certainly a mistake.
 */
@Rule(key = "outline-placeholder-required")
public class OutlinePlaceholderRequiredCheck extends BaseCheck {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("<([^>]+)>");

    @Override
    public void leaveScenario(ScenarioDefinition scenario) {
        if (!scenario.isOutline()) {
            return;
        }

        boolean hasPlaceholder = false;

        // Check scenario name
        if (containsPlaceholder(scenario.name())) {
            hasPlaceholder = true;
        }

        // Check scenario description
        if (!hasPlaceholder && containsPlaceholder(scenario.description())) {
            hasPlaceholder = true;
        }

        // Check step text
        if (!hasPlaceholder) {
            for (StepDefinition step : scenario.steps()) {
                if (containsPlaceholder(step.text())) {
                    hasPlaceholder = true;
                    break;
                }
            }
        }

        if (!hasPlaceholder) {
            addIssue(scenario.position(),
                    "This Scenario Outline does not reference any <placeholder> variables. "
                            + "Add placeholders to parameterize the steps, or use a plain Scenario instead.");
        }
    }

    private static boolean containsPlaceholder(String text) {
        return text != null && PLACEHOLDER_PATTERN.matcher(text).find();
    }
}
