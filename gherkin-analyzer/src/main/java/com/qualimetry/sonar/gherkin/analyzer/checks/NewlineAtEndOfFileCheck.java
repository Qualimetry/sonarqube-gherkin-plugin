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
 * Checks that files end with a newline character.
 * <p>
 * POSIX defines a line as a sequence of zero or more non-newline characters
 * plus a terminating newline character. Files without a trailing newline may
 * cause issues with some tools and are inconsistent with POSIX standards.
 */
@Rule(key = "newline-at-end-of-file")
public class NewlineAtEndOfFileCheck extends BaseCheck {

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null) {
            return;
        }

        // Check if the file ends with a newline character
        if (!rawContent.isEmpty() && !rawContent.endsWith("\n")) {
            addFileIssue("Add a newline at the end of this file.");
        }
    }
}
