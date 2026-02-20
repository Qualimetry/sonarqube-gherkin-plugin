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
 * Checks that lines do not have trailing whitespace.
 * <p>
 * Trailing whitespace is unnecessary and can cause issues in version control
 * systems and when comparing files. Remove trailing spaces and tabs.
 */
@Rule(key = "no-trailing-whitespace")
public class NoTrailingWhitespaceCheck extends BaseCheck {

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null) {
            return;
        }

        String[] lines = rawContent.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineNum = i + 1;

            // Strip trailing \r left over from CRLF line endings so the check
            // works identically on LF and CRLF files.
            if (line.endsWith("\r")) {
                line = line.substring(0, line.length() - 1);
            }

            // Check for trailing whitespace (spaces or tabs)
            if (line.length() > 0) {
                char lastChar = line.charAt(line.length() - 1);
                if (lastChar == ' ' || lastChar == '\t') {
                    addLineIssue(lineNum, "Remove trailing whitespace.");
                }
            }
        }
    }
}
