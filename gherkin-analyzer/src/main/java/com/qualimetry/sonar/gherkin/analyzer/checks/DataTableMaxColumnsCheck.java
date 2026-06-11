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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.DataTableDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that data tables (in steps or Examples) do not exceed a
 * configurable column count.
 * <p>
 * Wide data tables are difficult to read and usually indicate that the
 * step or scenario is doing too much. The default limit is 10 columns.
 */
@Rule(key = "data-table-max-columns")
public class DataTableMaxColumnsCheck extends BaseCheck {

    private static final int DEFAULT_MAX_COLUMNS = 10;

    @RuleProperty(
            key = "maxColumns",
            description = "Maximum number of columns allowed in a data table",
            defaultValue = "" + DEFAULT_MAX_COLUMNS)
    private int maxColumns = DEFAULT_MAX_COLUMNS;

    public void setMaxColumns(int maxColumns) {
        this.maxColumns = maxColumns;
    }

    @Override
    public void visitStep(StepDefinition step) {
        if (step.dataTable() != null) {
            checkTable(step.dataTable(), step.position(), "Step");
        }
    }

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        if (examples.table() != null) {
            checkTable(examples.table(), examples.position(), "Examples");
        }
    }

    private void checkTable(DataTableDefinition table, TextPosition position, String elementType) {
        if (table.rows().isEmpty()) {
            return;
        }
        int columnCount = table.rows().get(0).size();
        if (columnCount > maxColumns) {
            addIssue(position,
                    elementType + " data table has " + columnCount + " columns, which exceeds the limit of "
                            + maxColumns + ". Consider reducing the number of columns.");
        }
    }
}
