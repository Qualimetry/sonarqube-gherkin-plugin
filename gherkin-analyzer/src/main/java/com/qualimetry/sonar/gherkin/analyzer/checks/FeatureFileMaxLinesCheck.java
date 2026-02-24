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
 * Checks that feature files do not exceed a configurable line count.
 * <p>
 * Feature files that grow too long become difficult to navigate and
 * maintain. This complements {@code scenario-count-limit} by also
 * catching files that are long due to large data tables, doc strings,
 * or verbose descriptions. The default limit is 300 lines.
 */
@Rule(key = "feature-file-max-lines")
public class FeatureFileMaxLinesCheck extends BaseCheck {

    private static final int DEFAULT_MAX_LINES = 300;

    @RuleProperty(
            key = "maxLines",
            description = "Maximum number of lines allowed in a feature file",
            defaultValue = "" + DEFAULT_MAX_LINES)
    private int maxLines = DEFAULT_MAX_LINES;

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null) {
            return;
        }

        int lineCount = countLines(rawContent);

        if (lineCount > maxLines) {
            addFileIssue(
                    "This file has " + lineCount + " lines, which exceeds the limit of "
                            + maxLines + ". Split it into smaller feature files.");
        }
    }

    private static int countLines(String content) {
        if (content.isEmpty()) {
            return 0;
        }
        int count = 1;
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                count++;
            }
        }
        // If the content ends with a newline, the last "line" is empty  - 
        // don't count it as an additional line.
        if (content.endsWith("\n")) {
            count--;
        }
        return count;
    }
}
