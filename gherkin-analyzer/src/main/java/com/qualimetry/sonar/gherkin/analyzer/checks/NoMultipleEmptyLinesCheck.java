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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Flags consecutive blank lines in feature files.
 * <p>
 * A single blank line is acceptable as a separator between elements,
 * but two or more consecutive blank lines add visual noise without
 * improving readability. When two or more consecutive blank lines are
 * detected, an issue is reported on each extra blank line in the group.
 * <p>
 * Uses raw content analysis via {@code getContext().getRawContent()}.
 */
@Rule(key = "no-multiple-empty-lines")
public class NoMultipleEmptyLinesCheck extends BaseCheck {

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null) {
            return;
        }

        String[] lines = rawContent.split("\\r?\\n", -1);
        boolean previousBlank = false;

        for (int i = 0; i < lines.length; i++) {
            boolean currentBlank = lines[i].trim().isEmpty();
            if (currentBlank && previousBlank) {
                // Report on the first blank line of the consecutive pair (i is 0-based
                // index of the second blank; 1-based that is i+1; one back is i).
                addLineIssue(i, "Remove this unnecessary blank line; only one consecutive blank line is allowed.");
            }
            previousBlank = currentBlank;
        }
    }
}
