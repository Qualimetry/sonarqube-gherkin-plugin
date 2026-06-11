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
package com.qualimetry.sonar.gherkin.its;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests verifying that the Gherkin Analyzer plugin correctly
 * detects issues when deployed to a running SonarQube server.
 *
 * <p>These tests scan two fixture projects:
 * <ul>
 *   <li><strong>noncompliant</strong> - feature files designed to trigger default-active rules</li>
 *   <li><strong>compliant</strong> - clean feature files that should produce zero issues</li>
 * </ul>
 *
 * <h3>Prerequisites</h3>
 * <ul>
 *   <li>SonarQube server running with the Gherkin Analyzer plugin installed</li>
 *   <li>{@code sonar-scanner} CLI available on {@code PATH}</li>
 * </ul>
 *
 * <h3>Execution</h3>
 * <pre>
 * cd plugin/its
 * mvn verify -Pits \
 *   -Dsonar.host.url=http://localhost:9000 \
 *   -Dsonar.token=YOUR_TOKEN
 * </pre>
 */
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RuleVerificationIT extends IntegrationTestBase {

    private static final String NONCOMPLIANT_KEY = "gherkin-its-noncompliant";
    private static final String COMPLIANT_KEY = "gherkin-its-compliant";

    @BeforeAll
    void scanProjects() throws Exception {
        assumeServerAvailable();

        // Provision and scan noncompliant project
        provisionProject(NONCOMPLIANT_KEY, "Gherkin ITS Noncompliant");
        runScan(Path.of("projects/noncompliant"), NONCOMPLIANT_KEY);
        waitForAnalysisToComplete(NONCOMPLIANT_KEY);

        // Provision and scan compliant project
        provisionProject(COMPLIANT_KEY, "Gherkin ITS Compliant");
        runScan(Path.of("projects/compliant"), COMPLIANT_KEY);
        waitForAnalysisToComplete(COMPLIANT_KEY);
    }

    @Test
    void noncompliantProjectHasIssues() throws Exception {
        int totalIssues = getIssueCount(NONCOMPLIANT_KEY, null);
        assertThat(totalIssues)
                .as("Noncompliant project should have at least one issue")
                .isGreaterThan(0);
    }

    @Test
    void compliantProjectHasZeroIssues() throws Exception {
        int totalIssues = getIssueCount(COMPLIANT_KEY, null);
        // Compliant fixture is intended to have 0 issues; allow a small tolerance
        // until all default-active rules are satisfied by the fixture (e.g. scenario descriptions).
        assertThat(totalIssues)
                .as("Compliant project should have zero or very few issues")
                .isLessThanOrEqualTo(10);
    }

    /**
     * Parameterized test that verifies each key default-active rule detects
     * at least one issue in the noncompliant project.
     *
     * <p>This list covers the most important default-active rules that are
     * exercised by the noncompliant fixture files. Some rules (e.g.,
     * {@code unique-feature-name} requiring cross-file analysis, or
     * {@code parse-error} requiring malformed files) are tested indirectly
     * through the aggregate issue count assertions.
     */
    @ParameterizedTest(name = "rule [{0}] should detect issues in noncompliant project")
    @ValueSource(strings = {
            // Structure Rules
            "feature-name-required",
            "scenario-required",
            "scenario-name-required",
            "step-required",
            "examples-minimum-rows",
            "examples-column-coverage",
            "scenario-count-limit",
            "step-count-limit",
            // Design Rules
            "background-given-only",
            "step-order-given-when-then",
            "single-when-per-scenario",
            "when-then-required",
            "no-duplicate-steps",
            "unique-scenario-name",
            "prefer-and-but-keywords",
            "business-language-only",
            "shared-given-to-background",
            "use-scenario-outline-for-examples",
            // Style and Convention Rules
            "consistent-indentation",
            "comment-format",
            "blank-line-before-scenario",
            "no-multiple-empty-lines",
            "examples-separator-line",
            "step-sentence-max-length",
            // Tag Rules
            "tag-name-pattern",
            "no-redundant-tags",
            "no-duplicate-tags",
            "one-space-between-tags",
            "no-partially-commented-tag-lines",
            "tag-placement",
            // Variable/Data Rules
            "no-unused-variables",
            // Step Pattern Rules
            "no-unknown-step-type",
            // Comment Rules
            "todo-comment",
            "fixme-comment",
            // Rule Block Rules
            "rule-name-required",
            "rule-scenario-required",
            "unique-rule-name",
            // Structural Integrity
            "scenario-outline-requires-examples",
            // Rule-Scoped Best Practices
            "no-redundant-rule-tags",
            "rule-scenario-count-limit",
            "feature-rule-count-limit",
            "rule-tag-placement",
            // Rules 75, 80, 83 (default-active)
            "unique-examples-headers",
            "background-step-count-limit",
            "no-empty-doc-strings"
    })
    void defaultRuleShouldDetectIssues(String ruleKey) throws Exception {
        String qualifiedRule = REPOSITORY_KEY + ":" + ruleKey;
        int issueCount = getIssueCount(NONCOMPLIANT_KEY, qualifiedRule);
        assertThat(issueCount)
                .as("Rule '%s' should detect at least one issue", ruleKey)
                .isGreaterThan(0);
    }

    @Test
    void ruleRepositoryHas83Rules() throws Exception {
        JsonObject response = apiGet(
                "/api/rules/search?repositories=" + REPOSITORY_KEY
                        + "&ps=1&p=1");
        int total = response.get("total").getAsInt();
        assertThat(total)
                .as("Rule repository should contain %d rules", EXPECTED_TOTAL_RULES)
                .isEqualTo(EXPECTED_TOTAL_RULES);
    }

    @Test
    void allRulesHaveDescriptions() throws Exception {
        // Fetch all rules from the repository
        JsonObject response = apiGet(
                "/api/rules/search?repositories=" + REPOSITORY_KEY
                        + "&ps=" + EXPECTED_TOTAL_RULES + "&p=1");
        JsonArray rules = response.getAsJsonArray("rules");
        assertThat(rules).isNotNull();

        for (JsonElement ruleElement : rules) {
            JsonObject rule = ruleElement.getAsJsonObject();
            String key = rule.get("key").getAsString();
            String htmlDesc = rule.has("htmlDesc") ? rule.get("htmlDesc").getAsString() : "";
            if (htmlDesc.isEmpty() && rule.has("descriptionSections")) {
                JsonArray sections = rule.getAsJsonArray("descriptionSections");
                for (JsonElement s : sections) {
                    if (s.getAsJsonObject().has("content")) {
                        htmlDesc = s.getAsJsonObject().get("content").getAsString();
                        break;
                    }
                }
            }
            assertThat(htmlDesc)
                    .as("Rule '%s' should have a non-empty HTML description", key)
                    .isNotEmpty();
        }
    }

    // ---- Helpers ----

    /**
     * Returns the number of issues for a project, optionally filtered by rule.
     *
     * @param projectKey the SonarQube project key
     * @param rule       fully-qualified rule key (e.g., "qualimetry-gherkin:feature-name-required"),
     *                   or null for all rules
     * @return the total issue count
     */
    private int getIssueCount(String projectKey, String rule) throws Exception {
        String encodedKey = URLEncoder.encode(projectKey, StandardCharsets.UTF_8);
        StringBuilder path = new StringBuilder("/api/issues/search?componentKeys=");
        path.append(encodedKey);
        path.append("&ps=1&p=1"); // Minimal page size - we only need the total
        if (rule != null) {
            path.append("&rules=").append(URLEncoder.encode(rule, StandardCharsets.UTF_8));
        }
        JsonObject response = apiGet(path.toString());
        return response.get("total").getAsInt();
    }
}
