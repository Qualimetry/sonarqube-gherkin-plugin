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
package com.qualimetry.sonar.gherkin.analyzer.testing;

/**
 * Represents an expected issue extracted from a {@code # Noncompliant} annotation
 * in a test fixture file. Used by {@link CheckVerifier} to compare expected
 * issues against actual issues produced by a check.
 *
 * @param line        the 1-based line number where the issue is expected (required)
 * @param message     the expected issue message, or {@code null} if only the line number is asserted
 * @param startColumn the expected start column (1-based), or {@code null} if not asserted
 * @param endColumn   the expected end column (1-based), or {@code null} if not asserted
 */
public record TestIssue(
        int line,
        String message,
        Integer startColumn,
        Integer endColumn) {

    /**
     * Creates a {@code TestIssue} asserting only the line number.
     *
     * @param line the 1-based line number
     */
    public TestIssue(int line) {
        this(line, null, null, null);
    }

    /**
     * Creates a {@code TestIssue} asserting line number and message.
     *
     * @param line    the 1-based line number
     * @param message the expected issue message
     */
    public TestIssue(int line, String message) {
        this(line, message, null, null);
    }
}
