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

import java.util.regex.Pattern;

/**
 * Flags tag lines that also contain inline comments.
 * <p>
 * In Gherkin, lines starting with {@code @} are tag lines. If such a line also
 * contains a {@code #} character, it means some tags on that line are commented out.
 * This is confusing because Gherkin does not support inline comments on tag lines
 * — the {@code #} and everything after it becomes part of the last tag's text.
 * Tags should either all be active or moved to a proper comment line.
 * <p>
 * Uses raw content analysis via {@code getContext().getRawContent()}.
 */
@Rule(key = "no-partially-commented-tag-lines")
public class NoPartiallyCommentedTagLinesCheck extends BaseCheck {

    /**
     * Matches lines that start with optional whitespace, then {@code @},
     * and somewhere later contain a {@code #} character.
     */
    private static final Pattern TAG_LINE_WITH_COMMENT = Pattern.compile("^\\s*@.*#");

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null) {
            return;
        }

        String[] lines = rawContent.split("\\r?\\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (TAG_LINE_WITH_COMMENT.matcher(lines[i]).find()) {
                addLineIssue(i + 1,
                        "Do not mix tags and comments on the same line; move the comment to a separate line.");
            }
        }
    }
}
