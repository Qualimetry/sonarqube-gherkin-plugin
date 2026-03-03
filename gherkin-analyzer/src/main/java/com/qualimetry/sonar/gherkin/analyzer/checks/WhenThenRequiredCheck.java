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
 * Checks that scenarios have at least one {@code When} and one {@code Then} step.
 * <p>
 * A well-formed scenario describes an action (When) and its expected outcome
 * (Then). Without both, the scenario is incomplete: missing When means no
 * action is tested; missing Then means no outcome is verified.
 */
@Rule(key = "when-then-required")
public class WhenThenRequiredCheck extends BaseCheck {

    @Override
    public void leaveScenario(ScenarioDefinition scenario) {
        boolean hasWhen = false;
        boolean hasThen = false;
        for (StepDefinition step : scenario.steps()) {
            if ("ACTION".equals(step.keywordType())) {
                hasWhen = true;
            } else if ("OUTCOME".equals(step.keywordType())) {
                hasThen = true;
            }
        }
        if (!hasWhen && !hasThen) {
            addIssue(scenario.position(),
                    "Add When and Then steps to this Scenario.");
        } else if (!hasWhen) {
            addIssue(scenario.position(),
                    "Add a When step to this Scenario.");
        } else if (!hasThen) {
            addIssue(scenario.position(),
                    "Add a Then step to this Scenario.");
        }
    }
}
