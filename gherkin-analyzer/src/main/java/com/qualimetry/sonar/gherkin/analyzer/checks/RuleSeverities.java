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

import java.util.Map;

/**
 * Canonical default severity per rule key, aligned with the SonarQube plugin
 * built-in quality profile. Shared by the SonarQube plugin and the IDE/LSP so
 * that installing both gives the same default rules and severities.
 * <p>
 * All five Sonar severity names are supported in config: {@value #BLOCKER},
 * {@value #CRITICAL}, {@value #MAJOR}, {@value #MINOR}, {@value #INFO}.
 * This map only uses CRITICAL, MAJOR, MINOR, INFO for defaults (no rule is
 * assigned BLOCKER by default). Users may set "blocker" in the IDE for any rule.
 */
public final class RuleSeverities {

    public static final String BLOCKER = "BLOCKER";
    public static final String CRITICAL = "CRITICAL";
    public static final String MAJOR = "MAJOR";
    public static final String MINOR = "MINOR";
    public static final String INFO = "INFO";

    private static final Map<String, String> SEVERITIES = Map.ofEntries(
            // Structure Rules (1-10)
            Map.entry("feature-file-required", MAJOR),
            Map.entry("feature-name-required", CRITICAL),
            Map.entry("feature-description-recommended", INFO),
            Map.entry("scenario-required", MAJOR),
            Map.entry("scenario-name-required", CRITICAL),
            Map.entry("step-required", MAJOR),
            Map.entry("examples-minimum-rows", CRITICAL),
            Map.entry("examples-column-coverage", CRITICAL),
            Map.entry("scenario-count-limit", MAJOR),
            Map.entry("step-count-limit", MAJOR),
            // Design Rules (11-21)
            Map.entry("background-given-only", CRITICAL),
            Map.entry("shared-given-to-background", MAJOR),
            Map.entry("step-order-given-when-then", CRITICAL),
            Map.entry("single-when-per-scenario", MAJOR),
            Map.entry("when-then-required", CRITICAL),
            Map.entry("unique-feature-name", MAJOR),
            Map.entry("unique-scenario-name", MAJOR),
            Map.entry("no-duplicate-steps", CRITICAL),
            Map.entry("use-scenario-outline-for-examples", MAJOR),
            Map.entry("business-language-only", MAJOR),
            Map.entry("consistent-feature-language", MAJOR),
            // Style and Convention Rules (22-33)
            Map.entry("consistent-indentation", MINOR),
            Map.entry("no-tab-characters", MINOR),
            Map.entry("no-trailing-whitespace", MINOR),
            Map.entry("newline-at-end-of-file", MINOR),
            Map.entry("no-byte-order-mark", MAJOR),
            Map.entry("consistent-line-endings", MINOR),
            Map.entry("file-name-convention", MINOR),
            Map.entry("comment-format", MINOR),
            Map.entry("prefer-and-but-keywords", MINOR),
            Map.entry("no-star-step-prefix", MINOR),
            Map.entry("examples-separator-line", MINOR),
            Map.entry("step-sentence-max-length", MAJOR),
            // Tag Rules (34-38)
            Map.entry("tag-name-pattern", MINOR),
            Map.entry("tag-permitted-values", MINOR),
            Map.entry("tag-placement", MINOR),
            Map.entry("no-redundant-tags", MINOR),
            Map.entry("no-examples-tags", MINOR),
            // Variable and Data Rules (39)
            Map.entry("no-unused-variables", MAJOR),
            // Step Pattern Rules (40-43)
            Map.entry("given-step-pattern", MINOR),
            Map.entry("when-step-pattern", MINOR),
            Map.entry("then-step-pattern", MINOR),
            Map.entry("no-unknown-step-type", MAJOR),
            // Comment and Marker Rules (44-46)
            Map.entry("todo-comment", INFO),
            Map.entry("fixme-comment", INFO),
            Map.entry("comment-pattern-match", MAJOR),
            // Spelling Rules (47)
            Map.entry("spelling-accuracy", INFO),
            // Parser Error Rules (48)
            Map.entry("parse-error", CRITICAL),
            // Rule Block Quality Rules (49-52)
            Map.entry("rule-name-required", CRITICAL),
            Map.entry("rule-scenario-required", MAJOR),
            Map.entry("unique-rule-name", MAJOR),
            Map.entry("rule-description-recommended", INFO),
            // Structural Integrity Rules (53-56)
            Map.entry("outline-placeholder-required", MAJOR),
            Map.entry("scenario-outline-requires-examples", MAJOR),
            Map.entry("background-needs-multiple-scenarios", MINOR),
            Map.entry("blank-line-before-scenario", MINOR),
            // Rule-Scoped Best Practices (57-60)
            Map.entry("rule-scenario-count-limit", MAJOR),
            Map.entry("feature-rule-count-limit", MAJOR),
            Map.entry("no-redundant-rule-tags", MINOR),
            Map.entry("rule-tag-placement", MINOR),
            // Advanced Quality (61-63)
            Map.entry("examples-name-when-multiple", MINOR),
            Map.entry("consistent-scenario-keyword", MINOR),
            Map.entry("no-duplicate-tags", MINOR),
            // Ecosystem Parity (64-69)
            Map.entry("no-multiple-empty-lines", MINOR),
            Map.entry("required-tags", MAJOR),
            Map.entry("no-restricted-tags", MAJOR),
            Map.entry("name-max-length", MINOR),
            Map.entry("one-space-between-tags", MINOR),
            Map.entry("no-partially-commented-tag-lines", MINOR),
            // Configurable Quality Thresholds (70-74)
            Map.entry("outline-single-example-row", MINOR),
            Map.entry("no-restricted-patterns", MAJOR),
            Map.entry("max-tags-per-element", MINOR),
            Map.entry("feature-file-max-lines", MINOR),
            Map.entry("data-table-max-columns", MINOR),
            // Rules 75-83
            Map.entry("unique-examples-headers", CRITICAL),
            Map.entry("no-empty-examples-cells", MAJOR),
            Map.entry("no-duplicate-scenario-bodies", MAJOR),
            Map.entry("no-conflicting-tags", MAJOR),
            Map.entry("no-commented-out-steps", MINOR),
            Map.entry("background-step-count-limit", MAJOR),
            Map.entry("feature-name-matches-filename", MINOR),
            Map.entry("scenario-description-recommended", INFO),
            Map.entry("no-empty-doc-strings", MINOR)
    );

    private RuleSeverities() {
        // utility class
    }

    /**
     * Returns the default Sonar severity for the rule (e.g. CRITICAL, MAJOR, MINOR, INFO).
     *
     * @param ruleKey the rule key
     * @return the severity string, or MAJOR if unknown
     */
    public static String getSeverity(String ruleKey) {
        return SEVERITIES.getOrDefault(ruleKey, MAJOR);
    }
}
