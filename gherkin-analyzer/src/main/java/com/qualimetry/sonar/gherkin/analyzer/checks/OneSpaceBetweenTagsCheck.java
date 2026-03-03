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
 * Checks that tag lines have exactly one space between consecutive tags.
 * <p>
 * On lines that contain tags (starting with {@code @} after optional whitespace),
 * consecutive {@code @}-prefixed tokens must be separated by exactly one space.
 * Multiple spaces or tabs between tags make the tag line harder to read.
 * <p>
 * Uses raw content analysis via {@code getContext().getRawContent()}.
 */
@Rule(key = "one-space-between-tags")
public class OneSpaceBetweenTagsCheck extends BaseCheck {

    /**
     * Matches a line that starts with optional whitespace and then an {@code @} symbol.
     */
    private static final Pattern TAG_LINE = Pattern.compile("^\\s*@");

    /**
     * Detects two or more whitespace characters between two tag tokens ({@code @word}).
     * Looks for a non-whitespace sequence starting with {@code @}, followed by 2+ spaces/tabs,
     * then another {@code @}.
     */
    private static final Pattern EXTRA_SPACING = Pattern.compile("@\\S+\\s{2,}@");

    /**
     * Detects missing space between two tag tokens ({@code @tag1@tag2}).
     * Matches a {@code @} followed by non-whitespace/non-{@code @} characters,
     * then immediately another {@code @}.
     */
    private static final Pattern MISSING_SPACING = Pattern.compile("@[^@\\s]+@");

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null) {
            return;
        }

        String[] lines = rawContent.split("\\r?\\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (TAG_LINE.matcher(line).find()
                    && (EXTRA_SPACING.matcher(line).find() || MISSING_SPACING.matcher(line).find())) {
                addLineIssue(i + 1, "Use exactly one space between tags on this line.");
            }
        }
    }
}
