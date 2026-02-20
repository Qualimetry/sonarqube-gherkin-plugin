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
 * Aggregated evidence for a single analysis rule, including metadata and
 * the results of running the rule against all of its test fixture files.
 * <p>
 * Used by {@link EvidenceReportGenerator} to produce per-rule sections in
 * the evidence report HTML and per-rule artifact directories.
 *
 * @param ruleKey         the rule key (e.g., {@code "step-required"})
 * @param displayName     human-readable display name (e.g., {@code "Step Required"})
 * @param severity        the rule severity (e.g., {@code "MAJOR"}, {@code "CRITICAL"})
 * @param defaultActive   whether the rule is active in the default quality profile
 * @param descriptionHtml the rendered HTML description of the rule, or {@code null} if unavailable
 * @param fixtureResults  the results of running the rule against each of its fixture files
 */
public record RuleEvidence(
        String ruleKey,
        String displayName,
        String severity,
        boolean defaultActive,
        String descriptionHtml,
        List<FixtureResult> fixtureResults) {

    /**
     * Returns {@code true} if all fixture files have an acceptable status
     * (no genuine failures). A rule is "verified" if every fixture is
     * PASS, REQUIRES_CONFIGURATION, or CROSS_RULE_REFERENCE.
     */
    public boolean allAcceptable() {
        return fixtureResults.stream().allMatch(FixtureResult::acceptable);
    }

    /**
     * Returns the number of fixtures that passed verification exactly.
     */
    public long passedCount() {
        return fixtureResults.stream().filter(FixtureResult::passed).count();
    }

    /**
     * Returns the number of fixtures that require non-default configuration.
     */
    public long requiresConfigCount() {
        return fixtureResults.stream()
                .filter(f -> FixtureResult.STATUS_REQUIRES_CONFIGURATION.equals(f.status()))
                .count();
    }

    /**
     * Returns the number of cross-rule reference fixtures.
     */
    public long crossRuleCount() {
        return fixtureResults.stream()
                .filter(f -> FixtureResult.STATUS_CROSS_RULE_REFERENCE.equals(f.status()))
                .count();
    }

    /**
     * Returns the number of genuine failures.
     */
    public long failedCount() {
        return fixtureResults.stream()
                .filter(f -> FixtureResult.STATUS_FAIL.equals(f.status()))
                .count();
    }

    /**
     * Returns the total number of fixture files evaluated.
     */
    public int fixtureCount() {
        return fixtureResults.size();
    }
}
