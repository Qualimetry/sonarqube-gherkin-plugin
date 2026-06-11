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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that Rule blocks do not exceed a configurable number of scenarios.
 * <p>
 * Rule blocks with too many scenarios suggest the business rule is too broad
 * and should be decomposed into smaller, more focused rules.
 */
@Rule(key = "rule-scenario-count-limit")
public class RuleScenarioCountLimitCheck extends BaseCheck {

    private static final int DEFAULT_MAX_SCENARIOS = 10;

    @RuleProperty(
            key = "maxScenarios",
            description = "Maximum number of scenarios allowed per Rule block",
            defaultValue = "" + DEFAULT_MAX_SCENARIOS)
    private int maxScenarios = DEFAULT_MAX_SCENARIOS;

    public void setMaxScenarios(int maxScenarios) {
        this.maxScenarios = maxScenarios;
    }

    @Override
    public void leaveRule(RuleDefinition rule) {
        int count = rule.scenarios().size();
        if (count > maxScenarios) {
            addIssue(rule.position(),
                    "This Rule has " + count + " scenarios, which exceeds the limit of "
                            + maxScenarios + ". Decompose it into smaller rules.");
        }
    }
}
