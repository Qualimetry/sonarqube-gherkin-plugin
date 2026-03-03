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
 * Checks that files do not start with a UTF-8 Byte Order Mark (BOM).
 * <p>
 * The UTF-8 BOM (U+FEFF) is unnecessary for UTF-8 files and can cause issues
 * with some tools and parsers. Remove the BOM from the beginning of the file.
 */
@Rule(key = "no-byte-order-mark")
public class NoByteOrderMarkCheck extends BaseCheck {

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null || rawContent.isEmpty()) {
            return;
        }

        // Check if the content starts with the UTF-8 BOM character
        if (rawContent.charAt(0) == '\uFEFF') {
            addLineIssue(1, "Remove the UTF-8 Byte Order Mark (BOM) from the beginning of this file.");
        }
    }
}
