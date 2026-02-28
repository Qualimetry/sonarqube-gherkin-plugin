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

/**
 * Checks that every Scenario includes a description explaining its purpose.
 * <p>
 * A description below the Scenario name provides valuable context about
 * the business case and the intent of the test. Without it, readers must
 * infer the purpose from the step sequence alone.
 */
@Rule(key = "scenario-description-recommended")
public class ScenarioDescriptionRecommendedCheck extends BaseCheck {

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        if (scenario.description() == null || scenario.description().isBlank()) {
            addIssue(scenario.position(),
                    "Add a description to this scenario to explain its purpose.");
        }
    }
}
