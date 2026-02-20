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
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that scenarios do not exceed a configurable number of steps.
 * <p>
 * Scenarios with too many steps are hard to read and often indicate that
 * the scenario is testing multiple behaviours at once. Consider splitting
 * it into smaller, more focused scenarios.
 */
@Rule(key = "step-count-limit")
public class StepCountLimitCheck extends BaseCheck {

    private static final int DEFAULT_MAX_STEPS = 12;

    @RuleProperty(
            key = "maxSteps",
            description = "Maximum number of steps allowed per Scenario",
            defaultValue = "" + DEFAULT_MAX_STEPS)
    private int maxSteps = DEFAULT_MAX_STEPS;

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    @Override
    public void leaveScenario(ScenarioDefinition scenario) {
        int stepCount = scenario.steps().size();
        if (stepCount > maxSteps) {
            addIssue(scenario.position(),
                    "This Scenario has " + stepCount + " steps, which exceeds the limit of "
                            + maxSteps + ". Split it into smaller scenarios.");
        }
    }
}
