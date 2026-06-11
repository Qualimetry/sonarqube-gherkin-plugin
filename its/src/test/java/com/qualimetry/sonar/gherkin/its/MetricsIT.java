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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests verifying that the Gherkin Analyzer plugin computes
 * and reports metrics correctly on a running SonarQube server.
 *
 * <p>Metrics verified:
 * <ul>
 *   <li>{@code ncloc} - non-comment lines of code</li>
 *   <li>{@code comment_lines} - number of comment lines</li>
 *   <li>{@code functions} - number of scenarios (mapped to SonarQube functions)</li>
 *   <li>{@code statements} - number of steps (mapped to SonarQube statements)</li>
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
class MetricsIT extends IntegrationTestBase {

    private static final String PROJECT_KEY = "gherkin-its-noncompliant";

    @BeforeAll
    void scanProject() throws Exception {
        assumeServerAvailable();

        provisionProject(PROJECT_KEY, "Gherkin ITS Noncompliant");
        runScan(Path.of("projects/noncompliant"), PROJECT_KEY);
        waitForAnalysisToComplete(PROJECT_KEY);
    }

    @Test
    void projectHasGherkinMetrics() throws Exception {
        String encodedKey = URLEncoder.encode(PROJECT_KEY, StandardCharsets.UTF_8);
        JsonObject response = apiGet(
                "/api/measures/component?component=" + encodedKey
                        + "&metricKeys=ncloc,comment_lines,functions,statements");
        JsonObject component = response.getAsJsonObject("component");
        assertThat(component).isNotNull();

        JsonArray measures = component.getAsJsonArray("measures");
        assertThat(measures)
                .as("Project should have metric measures")
                .isNotNull()
                .isNotEmpty();

        assertMetricIsPositive(measures, "ncloc");
        assertMetricIsPositive(measures, "comment_lines");
        assertMetricIsPositive(measures, "functions");
        assertMetricIsPositive(measures, "statements");
    }

    @Test
    void nclocIsReasonable() throws Exception {
        String encodedKey = URLEncoder.encode(PROJECT_KEY, StandardCharsets.UTF_8);
        JsonObject response = apiGet(
                "/api/measures/component?component=" + encodedKey
                        + "&metricKeys=ncloc");
        JsonObject component = response.getAsJsonObject("component");
        JsonArray measures = component.getAsJsonArray("measures");

        int ncloc = getMetricValue(measures, "ncloc");
        // The noncompliant project has ~20 feature files with multiple scenarios
        assertThat(ncloc)
                .as("NCLOC should be at least 50 for the noncompliant project")
                .isGreaterThanOrEqualTo(50);
    }

    @Test
    void functionsCountReflectsScenarios() throws Exception {
        String encodedKey = URLEncoder.encode(PROJECT_KEY, StandardCharsets.UTF_8);
        JsonObject response = apiGet(
                "/api/measures/component?component=" + encodedKey
                        + "&metricKeys=functions");
        JsonObject component = response.getAsJsonObject("component");
        JsonArray measures = component.getAsJsonArray("measures");

        int functions = getMetricValue(measures, "functions");
        // The noncompliant project has at least 10 scenarios across all feature files
        assertThat(functions)
                .as("Functions metric (scenario count) should be at least 10")
                .isGreaterThanOrEqualTo(10);
    }

    // ---- Helpers ----

    private void assertMetricIsPositive(JsonArray measures, String metricKey) {
        int value = getMetricValue(measures, metricKey);
        assertThat(value)
                .as("Metric '%s' should be greater than 0", metricKey)
                .isGreaterThan(0);
    }

    private int getMetricValue(JsonArray measures, String metricKey) {
        for (JsonElement element : measures) {
            JsonObject measure = element.getAsJsonObject();
            if (metricKey.equals(measure.get("metric").getAsString())) {
                return Integer.parseInt(measure.get("value").getAsString());
            }
        }
        throw new AssertionError("Metric '" + metricKey + "' not found in measures");
    }
}
