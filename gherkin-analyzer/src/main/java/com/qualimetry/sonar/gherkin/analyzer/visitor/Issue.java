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
package com.qualimetry.sonar.gherkin.analyzer.visitor;

import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;

/**
 * Represents an issue found by a check during analysis.
 *
 * @param ruleKey   the key of the rule that raised this issue (derived from the check's {@code @Rule} annotation)
 * @param message   the human-readable issue message
 * @param position  the precise text position of the issue, or {@code null} for file-level issues
 * @param line      the line number of the issue (1-based), or {@code null} for file-level issues
 * @param cost      the optional remediation effort cost, or {@code null} if not applicable
 * @param endColumn the 1-based column of the last character of the issue range (same line as position), or {@code null} to use default range
 */
public record Issue(
        String ruleKey,
        String message,
        TextPosition position,
        Integer line,
        Double cost,
        Integer endColumn) {
}
