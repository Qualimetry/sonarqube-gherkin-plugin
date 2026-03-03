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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that a blank line precedes each {@code Examples} section.
 * <p>
 * A blank separator line before the Examples keyword improves readability
 * by visually separating the scenario steps from the data table.
 */
@Rule(key = "examples-separator-line")
public class ExamplesSeparatorLineCheck extends BaseCheck {

    private String[] lines;

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent != null) {
            lines = rawContent.split("\\r?\\n", -1);
        } else {
            lines = null;
        }
    }

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        if (lines == null) {
            return;
        }
        int examplesLine = examples.position().line(); // 1-based
        if (examplesLine <= 1) {
            return; // first line in file, nothing to check
        }
        // Check the line before Examples (0-based index = examplesLine - 2)
        int prevIndex = examplesLine - 2;
        if (prevIndex >= 0 && prevIndex < lines.length) {
            String prevLine = lines[prevIndex].trim();
            if (!prevLine.isEmpty()) {
                addIssue(examples.position(),
                        "Add a blank line before this Examples section.");
            }
        }
    }
}
