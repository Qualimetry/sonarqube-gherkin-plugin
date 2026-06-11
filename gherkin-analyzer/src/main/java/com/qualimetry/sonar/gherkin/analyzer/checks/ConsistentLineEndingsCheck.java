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
import org.sonar.check.RuleProperty;

/**
 * Checks that all lines use consistent line endings.
 * <p>
 * Files should use consistent line endings throughout. Mixing LF, CRLF, and CR
 * can cause issues in version control systems and when viewing files on
 * different platforms.
 */
@Rule(key = "consistent-line-endings")
public class ConsistentLineEndingsCheck extends BaseCheck {

    private static final String DEFAULT_LINE_ENDING = "LF";

    @RuleProperty(
            key = "lineEnding",
            description = "Expected line ending type: LF, CRLF, or CR",
            defaultValue = DEFAULT_LINE_ENDING)
    private String lineEnding = DEFAULT_LINE_ENDING;

    public void setLineEnding(String lineEnding) {
        this.lineEnding = lineEnding != null ? lineEnding : DEFAULT_LINE_ENDING;
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null || rawContent.isEmpty()) {
            return;
        }

        // Determine expected line ending sequence
        String expectedSequence;
        if ("CRLF".equalsIgnoreCase(lineEnding)) {
            expectedSequence = "\r\n";
        } else if ("CR".equalsIgnoreCase(lineEnding)) {
            expectedSequence = "\r";
        } else {
            // Default to LF
            expectedSequence = "\n";
        }

        // Split content by all possible line endings to detect inconsistencies
        // We'll scan character by character to find mismatches
        int lineNum = 1;
        int i = 0;
        while (i < rawContent.length()) {
            char ch = rawContent.charAt(i);
            
            if (ch == '\n') {
                // Found LF
                if (!"\n".equals(expectedSequence)) {
                    addLineIssue(lineNum,
                            String.format("Expected %s line ending, but found LF.", lineEnding));
                }
                lineNum++;
                i++;
            } else if (ch == '\r') {
                // Could be CR or CRLF
                if (i + 1 < rawContent.length() && rawContent.charAt(i + 1) == '\n') {
                    // Found CRLF
                    if (!"\r\n".equals(expectedSequence)) {
                        addLineIssue(lineNum,
                                String.format("Expected %s line ending, but found CRLF.", lineEnding));
                    }
                    i += 2; // Skip both characters
                } else {
                    // Found CR alone
                    if (!"\r".equals(expectedSequence)) {
                        addLineIssue(lineNum,
                                String.format("Expected %s line ending, but found CR.", lineEnding));
                    }
                    i++;
                }
                lineNum++;
            } else {
                i++;
            }
        }
    }
}
