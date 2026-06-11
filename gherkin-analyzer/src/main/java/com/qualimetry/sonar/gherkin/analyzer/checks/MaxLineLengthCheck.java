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
 * Checks that no line exceeds a configurable maximum length.
 * <p>
 * Long lines are hard to read, especially in side-by-side diffs and code
 * review tools. Every raw line of the file is checked, including steps,
 * data tables, comments, and doc strings. The default limit is 120 characters.
 */
@Rule(key = "max-line-length")
public class MaxLineLengthCheck extends BaseCheck {

    private static final int DEFAULT_MAX_LENGTH = 120;

    @RuleProperty(
            key = "maxLength",
            description = "Maximum number of characters allowed in a line",
            defaultValue = "" + DEFAULT_MAX_LENGTH)
    private int maxLength = DEFAULT_MAX_LENGTH;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null) {
            return;
        }

        String[] lines = rawContent.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // Strip trailing \r left over from CRLF line endings so the check
            // works identically on LF and CRLF files.
            if (line.endsWith("\r")) {
                line = line.substring(0, line.length() - 1);
            }

            if (line.length() > maxLength) {
                addLineIssue(i + 1, "Shorten this line (" + line.length()
                        + " characters; maximum allowed is " + maxLength + ").");
            }
        }
    }
}
