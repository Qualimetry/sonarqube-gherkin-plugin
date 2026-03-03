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

/**
 * Checks that {@code Examples} tables have at least one data row beyond the header.
 * <p>
 * An Examples table with only a header row (or no rows at all) provides no test
 * data to the Scenario Outline, making it effectively dead code. The table structure
 * combines header (row 0) and data rows (rows 1+), so at least 2 rows are required.
 */
@Rule(key = "examples-minimum-rows")
public class ExamplesMinimumRowsCheck extends BaseCheck {

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        if (examples.table() == null || examples.table().rows().size() < 2) {
            addIssue(examples.position(),
                    "Add at least one data row to this Examples table.");
        }
    }
}
