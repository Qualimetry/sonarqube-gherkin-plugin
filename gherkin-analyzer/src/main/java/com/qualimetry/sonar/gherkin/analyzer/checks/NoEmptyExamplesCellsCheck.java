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

import java.util.List;

/**
 * Checks that data cells in Examples tables are not empty.
 * <p>
 * Empty cells usually indicate incomplete test data and can result in
 * placeholder variables being substituted with empty strings, which may
 * cause misleading test results.
 */
@Rule(key = "no-empty-examples-cells")
public class NoEmptyExamplesCellsCheck extends BaseCheck {

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        if (examples.table() == null || examples.table().rows().size() < 2) {
            return;
        }

        List<List<String>> rows = examples.table().rows();
        List<String> headers = rows.get(0);

        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == null || row.get(j).isBlank()) {
                    String columnName = j < headers.size() ? headers.get(j) : "column " + (j + 1);
                    addIssue(examples.position(),
                            "Examples table has an empty cell in data row " + i
                                    + ", column \"" + columnName + "\".");
                }
            }
        }
    }
}
