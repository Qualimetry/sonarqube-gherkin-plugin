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

import java.util.Set;

/**
 * Checks for comments that contain Gherkin step keywords, which indicate
 * dead or abandoned behaviour definitions.
 * <p>
 * Commented-out steps should be removed or restored as active steps to
 * keep feature files clean and accurate.
 */
@Rule(key = "no-commented-out-steps")
public class NoCommentedOutStepsCheck extends BaseCheck {

    private static final Set<String> STEP_KEYWORDS =
            Set.of("Given", "When", "Then", "And", "But", "*");

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null) {
            return;
        }

        String[] lines = rawContent.split("\\r?\\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String trimmed = lines[i].trim();
            if (trimmed.startsWith("#")) {
                String commentText = trimmed.substring(1).trim();

                // Exclude language declarations
                if (commentText.startsWith("language:")) {
                    continue;
                }

                for (String keyword : STEP_KEYWORDS) {
                    if (commentText.startsWith(keyword + " ")
                            || commentText.equals(keyword)) {
                        addLineIssue(i + 1, "Remove or restore this commented-out step.");
                        break;
                    }
                }
            }
        }
    }
}
