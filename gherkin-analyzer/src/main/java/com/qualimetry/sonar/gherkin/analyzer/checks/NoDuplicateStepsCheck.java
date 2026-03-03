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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.Set;

/**
 * Checks that steps within a scenario or background are not duplicated.
 * <p>
 * Repeating the exact same step text within a single scenario or background
 * is usually a mistake. Each step should be unique and contribute a
 * distinct piece of behaviour to the test.
 */
@Rule(key = "no-duplicate-steps")
public class NoDuplicateStepsCheck extends BaseCheck {

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        checkForDuplicates(scenario.steps());
    }

    @Override
    public void visitBackground(BackgroundDefinition background) {
        checkForDuplicates(background.steps());
    }

    private void checkForDuplicates(java.util.List<StepDefinition> steps) {
        Set<String> seen = new HashSet<>();
        for (StepDefinition step : steps) {
            String normalizedText = step.text().trim();
            if (!seen.add(normalizedText)) {
                addIssue(step.position(),
                        "Remove this duplicate step. The same step text appears earlier in this block.");
            }
        }
    }
}
