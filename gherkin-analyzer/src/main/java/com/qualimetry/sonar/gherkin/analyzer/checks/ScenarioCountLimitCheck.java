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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that features do not exceed a configurable number of scenarios.
 * <p>
 * Features with too many scenarios become difficult to maintain and understand.
 * Consider splitting large features into multiple files organized by business
 * domain or user journey.
 */
@Rule(key = "scenario-count-limit")
public class ScenarioCountLimitCheck extends BaseCheck {

    private static final int DEFAULT_MAX_SCENARIOS = 12;

    @RuleProperty(
            key = "maxScenarios",
            description = "Maximum number of scenarios allowed per Feature",
            defaultValue = "" + DEFAULT_MAX_SCENARIOS)
    private int maxScenarios = DEFAULT_MAX_SCENARIOS;

    private int scenarioCount;

    public void setMaxScenarios(int maxScenarios) {
        this.maxScenarios = maxScenarios;
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        scenarioCount = 0;
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        scenarioCount++;
    }

    @Override
    public void leaveFeature(FeatureDefinition feature) {
        if (scenarioCount > maxScenarios) {
            addIssue(feature.position(),
                    "This Feature has " + scenarioCount + " scenarios, which exceeds the limit of "
                            + maxScenarios + ". Split it into smaller features.");
        }
    }
}
