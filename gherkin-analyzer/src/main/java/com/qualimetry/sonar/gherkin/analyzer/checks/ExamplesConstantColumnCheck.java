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
 * Checks that no Examples column carries the same value in every data row.
 * <p>
 * A constant column adds no variation between examples; the value should
 * be inlined into the step text and the column removed. Tables with fewer
 * than two data rows are skipped, and each Examples section of an outline
 * is judged independently.
 */
@Rule(key = "examples-constant-column")
public class ExamplesConstantColumnCheck extends BaseCheck {

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        if (examples.table() == null || examples.table().rows().size() < 3) {
            return;
        }

        List<List<String>> rows = examples.table().rows();
        List<String> headers = rows.get(0);

        for (int j = 0; j < headers.size(); j++) {
            String constantValue = constantColumnValue(rows, j);
            if (constantValue != null) {
                addIssue(examples.table().position(),
                        "Examples column \"" + headers.get(j) + "\" has the constant value \""
                                + constantValue + "\" in every data row. Inline the value into the "
                                + "steps and remove the column.");
            }
        }
    }

    private static String constantColumnValue(List<List<String>> rows, int columnIndex) {
        String first = null;
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            String value = columnIndex < row.size() ? row.get(columnIndex) : null;
            if (value == null || value.isBlank()) {
                return null;
            }
            if (first == null) {
                first = value;
            } else if (!first.equals(value)) {
                return null;
            }
        }
        return first;
    }
}
