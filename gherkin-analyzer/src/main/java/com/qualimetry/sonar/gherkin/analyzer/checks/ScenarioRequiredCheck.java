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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that every {@code Feature} contains at least one {@code Scenario}.
 * <p>
 * A Feature without scenarios provides no test coverage. Scenarios within
 * {@code Rule} sections count toward this requirement.
 */
@Rule(key = "scenario-required")
public class ScenarioRequiredCheck extends BaseCheck {

    @Override
    public void leaveFeature(FeatureDefinition feature) {
        boolean hasTopLevelScenarios = !feature.scenarios().isEmpty();
        boolean hasRuleScenarios = feature.rules().stream()
                .anyMatch(rule -> !rule.scenarios().isEmpty());

        if (!hasTopLevelScenarios && !hasRuleScenarios) {
            addIssue(feature.position(), "Add at least one Scenario to this Feature.");
        }
    }
}
