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
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that every {@code Scenario}, {@code Scenario Outline}, and
 * {@code Background} contains at least one step.
 * <p>
 * A scenario or background without steps serves no purpose and indicates
 * an incomplete test specification that should be finished or removed.
 */
@Rule(key = "step-required")
public class StepRequiredCheck extends BaseCheck {

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        if (scenario.steps().isEmpty()) {
            addIssue(scenario.position(), "Add at least one step to this Scenario.");
        }
    }

    @Override
    public void visitBackground(BackgroundDefinition background) {
        if (background.steps().isEmpty()) {
            addIssue(background.position(), "Add at least one step to this Background.");
        }
    }
}
