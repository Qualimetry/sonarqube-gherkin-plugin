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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.Comment;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that comments follow the proper format with a space after the hash symbol.
 * <p>
 * Comments should start with {@code #} followed by a space, or be just {@code #}
 * for empty comments. Comments like {@code #comment} (without space) are flagged.
 */
@Rule(key = "comment-format")
public class CommentFormatCheck extends BaseCheck {

    @Override
    public void visitComment(Comment comment) {
        String text = comment.text().stripLeading();

        // Check if comment is properly formatted:
        // - Must start with "#"
        // - If there's content after "#", it must start with a space
        // - Empty comments (just "#") are allowed
        if (!text.startsWith("#")) {
            return;
        }

        // If comment has content after "#", it must start with a space
        if (text.length() > 1 && text.charAt(1) != ' ') {
            addIssue(comment.position(),
                    "Add a space after the '#' symbol in this comment.");
        }
    }
}
