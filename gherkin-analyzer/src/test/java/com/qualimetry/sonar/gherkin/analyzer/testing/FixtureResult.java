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

import java.util.List;

/**
 * Structured result of running a single check against a single fixture file.
 * Used by {@link EvidenceReportGenerator} to build human-reviewable evidence
 * reports that demonstrate test correctness and independent authorship.
 * <p>
 * Each result carries a {@link #status} classification:
 * <ul>
 *   <li>{@link #STATUS_PASS} — expected and actual issues match exactly</li>
 *   <li>{@link #STATUS_REQUIRES_CONFIGURATION} — the check has configurable
 *       {@code @RuleProperty} parameters and this fixture needs non-default
 *       values; it is verified by the rule's individual test class</li>
 *   <li>{@link #STATUS_CROSS_RULE_REFERENCE} — the fixture contains annotations
 *       for a related rule's concern (e.g., rule-level tag checks); it is
 *       verified by that rule's test class using {@code verifyNoIssues()}</li>
 *   <li>{@link #STATUS_FAIL} — a genuine mismatch between expected and actual
 *       issues that indicates a real test failure</li>
 * </ul>
 *
 * @param fixturePath     the classpath resource path of the fixture file
 * @param fixtureContent  the raw text content of the fixture file
 * @param expectedIssues  issues extracted from {@code # Noncompliant} annotations
 * @param actualIssues    issues actually produced by the check
 * @param status          classification status (one of the {@code STATUS_*} constants)
 * @param mismatches      human-readable descriptions of any differences between
 *                        expected and actual issues (empty when status is PASS)
 */
public record FixtureResult(
        String fixturePath,
        String fixtureContent,
        List<TestIssue> expectedIssues,
        List<TestIssue> actualIssues,
        String status,
        List<String> mismatches) {

    /** Expected and actual issues match exactly. */
    public static final String STATUS_PASS = "PASS";

    /** Genuine mismatch — a real test failure. */
    public static final String STATUS_FAIL = "FAIL";

    /** Check has {@code @RuleProperty} parameters; this fixture needs non-default
     *  values and is verified by the rule's individual test class. */
    public static final String STATUS_REQUIRES_CONFIGURATION = "REQUIRES_CONFIGURATION";

    /** Fixture annotations are for a related rule's concern; this check correctly
     *  produces no issues and is verified by the related rule's test class. */
    public static final String STATUS_CROSS_RULE_REFERENCE = "CROSS_RULE_REFERENCE";

    /** Check reports a file-level issue (line 0) which cannot use {@code # Noncompliant}
     *  annotations; the rule's individual test class verifies via manual assertions. */
    public static final String STATUS_FILE_LEVEL_CHECK = "FILE_LEVEL_CHECK";

    /**
     * Returns {@code true} if the fixture passed verification exactly.
     */
    public boolean passed() {
        return STATUS_PASS.equals(status);
    }

    /**
     * Returns {@code true} if the fixture status is acceptable (not a genuine failure).
     * This includes PASS, REQUIRES_CONFIGURATION, and CROSS_RULE_REFERENCE.
     */
    public boolean acceptable() {
        return !STATUS_FAIL.equals(status);
    }

    /**
     * Returns just the file name portion of the fixture path.
     * For example, {@code "checks/step-required/noncompliant.feature"}
     * returns {@code "noncompliant.feature"}.
     */
    public String fixtureName() {
        int lastSlash = fixturePath.lastIndexOf('/');
        return lastSlash >= 0 ? fixturePath.substring(lastSlash + 1) : fixturePath;
    }

    /**
     * Returns a new {@code FixtureResult} with the status reclassified.
     * All other fields are preserved.
     *
     * @param newStatus the new status classification
     * @return a new instance with the updated status
     */
    public FixtureResult withStatus(String newStatus) {
        return new FixtureResult(fixturePath, fixtureContent, expectedIssues,
                actualIssues, newStatus, mismatches);
    }
}
