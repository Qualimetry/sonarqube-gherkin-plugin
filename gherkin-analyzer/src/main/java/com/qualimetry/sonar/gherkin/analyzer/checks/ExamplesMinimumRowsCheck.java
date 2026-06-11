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
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that {@code Examples} tables have a configurable minimum number of
 * data rows beyond the header.
 * <p>
 * An Examples table with too few data rows barely exercises the Scenario
 * Outline; with none at all it is effectively dead code. The table structure
 * combines header (row 0) and data rows (rows 1+).
 */
@Rule(key = "examples-minimum-rows")
public class ExamplesMinimumRowsCheck extends BaseCheck {

    private static final int DEFAULT_MINIMUM_DATA_ROWS = 2;

    @RuleProperty(
            key = "minimumDataRows",
            description = "Minimum number of data rows required per Examples table",
            defaultValue = "" + DEFAULT_MINIMUM_DATA_ROWS)
    private int minimumDataRows = DEFAULT_MINIMUM_DATA_ROWS;

    public void setMinimumDataRows(int minimumDataRows) {
        this.minimumDataRows = minimumDataRows;
    }

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        int dataRows = examples.table() == null ? 0 : Math.max(0, examples.table().rows().size() - 1);
        if (dataRows < minimumDataRows) {
            addIssue(examples.position(),
                    "This Examples table has " + dataRows + " data row(s); at least "
                            + minimumDataRows + " are required.");
        }
    }
}
