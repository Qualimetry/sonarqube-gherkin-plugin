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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Flags Scenario Outlines where every Examples section has only a small
 * number of data rows, making the parameterization unnecessary.
 * <p>
 * A Scenario Outline where every Examples section has at most {@code maxDataRows}
 * data rows (default: 1) is functionally equivalent to a plain Scenario with
 * hardcoded values. The Outline/Examples syntax adds structural complexity
 * without meaningful parameterization benefit.
 */
@Rule(key = "outline-single-example-row")
public class OutlineSingleExampleRowCheck extends BaseCheck {

    private static final int DEFAULT_MAX_DATA_ROWS = 1;

    @RuleProperty(
            key = "maxDataRows",
            description = "Maximum data rows per Examples section that triggers the issue (all must be at or below this threshold)",
            defaultValue = "" + DEFAULT_MAX_DATA_ROWS)
    private int maxDataRows = DEFAULT_MAX_DATA_ROWS;

    public void setMaxDataRows(int maxDataRows) {
        this.maxDataRows = maxDataRows;
    }

    @Override
    public void leaveScenario(ScenarioDefinition scenario) {
        if (!scenario.isOutline()) {
            return;
        }
        if (scenario.examples().isEmpty()) {
            return;
        }

        boolean allBelowThreshold = true;
        for (ExamplesDefinition examples : scenario.examples()) {
            if (examples.table() == null) {
                continue;
            }
            // rows includes the header row; data rows = total - 1
            int dataRows = examples.table().rows().size() - 1;
            if (dataRows > maxDataRows) {
                allBelowThreshold = false;
                break;
            }
        }

        if (allBelowThreshold) {
            addIssue(scenario.position(),
                    "This Scenario Outline has " + scenario.examples().size()
                            + " Examples section(s), each with at most " + maxDataRows
                            + " data row(s). Consider using a plain Scenario instead.");
        }
    }
}
