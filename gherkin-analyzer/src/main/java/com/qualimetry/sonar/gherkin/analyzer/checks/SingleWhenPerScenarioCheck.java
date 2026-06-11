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

/**
 * Checks that each scenario contains exactly one {@code When} step.
 * <p>
 * A scenario should test a single action. Having multiple When steps
 * usually indicates the scenario is testing multiple behaviours and
 * should be split into separate scenarios.
 */
@Rule(key = "single-when-per-scenario")
public class SingleWhenPerScenarioCheck extends BaseCheck {

    @Override
    public void leaveScenario(ScenarioDefinition scenario) {
        int whenCount = 0;
        for (StepDefinition step : scenario.steps()) {
            if ("ACTION".equals(step.keywordType())) {
                whenCount++;
            }
        }
        if (whenCount > 1) {
            addIssue(scenario.position(),
                    "This Scenario has " + whenCount
                            + " When steps. Reduce to a single When step per Scenario.");
        }
    }
}
