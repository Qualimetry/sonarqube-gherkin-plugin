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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks that column headers in Examples tables are unique.
 * <p>
 * Duplicate column headers create ambiguous variable substitution - the
 * test framework cannot determine which column to use when a step references
 * a placeholder that matches multiple headers. This is always a defect.
 */
@Rule(key = "unique-examples-headers")
public class UniqueExamplesHeadersCheck extends BaseCheck {

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        if (examples.table() == null || examples.table().rows().isEmpty()) {
            return;
        }

        List<String> headers = examples.table().rows().get(0);
        Set<String> seen = new HashSet<>();
        for (String header : headers) {
            String trimmed = header.trim();
            if (!seen.add(trimmed)) {
                addIssue(examples.position(),
                        "Remove duplicate Examples header \"" + trimmed + "\".");
            }
        }
    }
}
