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
 * Checks that comments do not contain fix-me markers.
 * <p>
 * Such comments indicate known defects that should be tracked in an
 * issue tracker rather than left as comments in feature files. Leaving
 * them in Gherkin files risks them being forgotten.
 */
@Rule(key = "fixme-comment")
public class FixmeCommentCheck extends BaseCheck {

    @Override
    public void visitComment(Comment comment) {
        if (comment.text().toLowerCase().contains("fixme")) {
            addIssue(comment.position(),
                    "Address or remove this FIXME comment.");
        }
    }
}
