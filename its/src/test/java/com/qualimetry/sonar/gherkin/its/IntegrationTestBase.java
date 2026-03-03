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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Base class for Gherkin plugin integration tests against a running SonarQube server.
 *
 * <p>Provides helper methods for:
 * <ul>
 *   <li>Checking server availability (tests are skipped if unavailable)</li>
 *   <li>Running sonar-scanner against fixture projects</li>
 *   <li>Polling the Compute Engine until analysis completes</li>
 *   <li>Provisioning and deleting projects via the Web API</li>
 *   <li>Making authenticated GET/POST requests to the SonarQube REST API</li>
 * </ul>
 *
 * <h3>System Properties</h3>
 * <dl>
 *   <dt>{@code sonar.host.url}</dt><dd>Base URL of the SonarQube server (default: http://localhost:9000)</dd>
 *   <dt>{@code sonar.token}</dt><dd>Authentication token for the SonarQube API</dd>
 * </dl>
 *
 * <h3>Prerequisites</h3>
 * <ul>
 *   <li>SonarQube server running with the Gherkin Analyzer plugin installed</li>
 *   <li>{@code sonar-scanner} CLI available on {@code PATH}</li>
 * </ul>
 */
abstract class IntegrationTestBase {

    /** Base URL of the SonarQube server. */
    protected static final String SONAR_URL =
            System.getProperty("sonar.host.url", "http://localhost:9000");

    /** Authentication token for SonarQube API calls. */
    protected static final String SONAR_TOKEN =
            System.getProperty("sonar.token", "");

    /** Repository key for the Gherkin Analyzer plugin. */
    protected static final String REPOSITORY_KEY = "qualimetry-gherkin";

    /** Expected total number of rules in the plugin. */
    protected static final int EXPECTED_TOTAL_RULES = 83;

    /** Expected number of default-active rules in the quality profile. */
    protected static final int EXPECTED_DEFAULT_ACTIVE_RULES = 53;

    /** Quality profile name. */
    protected static final String PROFILE_NAME = "Qualimetry Gherkin";

    /** Gherkin language key in SonarQube. */
    protected static final String LANGUAGE_KEY = "gherkin";

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);
    private static final int CE_POLL_MAX_ATTEMPTS = 60;
    private static final long CE_POLL_INTERVAL_MS = 2000;
    /** Fixed wait when CE API is not available (e.g. 403). */
    private static final long CE_FALLBACK_WAIT_MS = 180_000;
    private static final long SCANNER_TIMEOUT_MINUTES = 5;

    /**
     * Assumes a SonarQube server is reachable and UP. Skips the test otherwise.
     * Call this from {@code @BeforeAll} or {@code @BeforeEach} to gracefully
     * skip integration tests when no server is available.
     */
    protected static void assumeServerAvailable() {
        try {
            HttpRequest request = newGetRequest("/api/system/status");
            HttpResponse<String> response = HTTP_CLIENT.send(request,
                    HttpResponse.BodyHandlers.ofString());
            Assumptions.assumeTrue(response.statusCode() == 200,
                    "SonarQube server not available at " + SONAR_URL);
            JsonObject body = GSON.fromJson(response.body(), JsonObject.class);
            Assumptions.assumeTrue("UP".equals(body.get("status").getAsString()),
                    "SonarQube server is not in UP status");
        } catch (Exception e) {
            Assumptions.assumeTrue(false,
                    "Cannot connect to SonarQube server at " + SONAR_URL + ": " + e.getMessage());
        }
    }

    /**
     * Executes sonar-scanner against the given project directory.
     *
     * @param projectDir  path to the project directory (containing sonar-project.properties)
     * @param projectKey  SonarQube project key to use for the scan
     * @throws IOException          if the scanner process cannot be started
     * @throws InterruptedException if the wait is interrupted
     */
    protected static void runScan(Path projectDir, String projectKey)
            throws IOException, InterruptedException {
        String scanner = getScannerCommand();
        ProcessBuilder pb = new ProcessBuilder(
                scanner,
                "-Dsonar.projectKey=" + projectKey,
                "-Dsonar.host.url=" + SONAR_URL,
                "-Dsonar.token=" + SONAR_TOKEN,
                "-Dsonar.projectBaseDir=" + projectDir.toAbsolutePath(),
                "-Dsonar.inclusions=**/*.feature",
                "-Dsonar.gherkin.file.suffixes=feature"
        );
        pb.directory(projectDir.toFile());
        pb.inheritIO();
        Process process = pb.start();
        boolean finished = process.waitFor(SCANNER_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException(
                    "sonar-scanner timed out after " + SCANNER_TIMEOUT_MINUTES + " minutes");
        }
        if (process.exitValue() != 0) {
            throw new RuntimeException(
                    "sonar-scanner exited with code " + process.exitValue());
        }
    }

    /**
     * Waits for the Compute Engine analysis to complete for the given project.
     * Polls the CE activity API every 2 seconds, up to 2 minutes. If the CE API
     * returns 403 (e.g. token lacks permission), falls back to a fixed wait so
     * tests can run against servers where CE is not exposed.
     *
     * @param projectKey the SonarQube project key
     * @throws Exception if the analysis does not complete within the timeout
     */
    protected static void waitForAnalysisToComplete(String projectKey) throws Exception {
        String encodedKey = URLEncoder.encode(projectKey, StandardCharsets.UTF_8);
        String path = "/api/ce/activity?component=" + encodedKey + "&status=PENDING,IN_PROGRESS";
        for (int attempt = 0; attempt < CE_POLL_MAX_ATTEMPTS; attempt++) {
            try {
                JsonObject response = apiGet(path);
                JsonArray tasks = response.getAsJsonArray("tasks");
                if (tasks == null || tasks.isEmpty()) {
                    return; // No pending/in-progress tasks - analysis complete
                }
            } catch (RuntimeException e) {
                if (e.getMessage() != null && e.getMessage().contains("403")) {
                    // Token cannot access CE API; wait fixed time and continue
                    Thread.sleep(CE_FALLBACK_WAIT_MS);
                    return;
                }
                throw e;
            }
            Thread.sleep(CE_POLL_INTERVAL_MS);
        }
        throw new RuntimeException(
                "Analysis did not complete within "
                        + (CE_POLL_MAX_ATTEMPTS * CE_POLL_INTERVAL_MS / 1000)
                        + " seconds for project: " + projectKey);
    }

    /**
     * Provisions a project on the SonarQube server.
     * Idempotent: returns successfully if the project already exists.
     *
     * @param projectKey unique project key
     * @param name       display name for the project
     * @throws Exception if the API call fails unexpectedly
     */
    protected static void provisionProject(String projectKey, String name) throws Exception {
        String body = "project=" + URLEncoder.encode(projectKey, StandardCharsets.UTF_8)
                + "&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
        HttpRequest request = newPostRequest("/api/projects/create", body);
        HttpResponse<String> response = HTTP_CLIENT.send(request,
                HttpResponse.BodyHandlers.ofString());
        // 200 = created, 400 = already exists (acceptable for idempotent ITS)
        if (response.statusCode() != 200 && response.statusCode() != 400) {
            throw new RuntimeException(
                    "Failed to provision project '" + projectKey + "': "
                            + response.statusCode() + " " + response.body());
        }
    }

    /**
     * Deletes a project from the SonarQube server.
     * Errors are silently ignored (project may not exist).
     *
     * @param projectKey the project key to delete
     */
    protected static void deleteProject(String projectKey) {
        try {
            String body = "project=" + URLEncoder.encode(projectKey, StandardCharsets.UTF_8);
            HttpRequest request = newPostRequest("/api/projects/delete", body);
            HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            // Ignore - project may not exist or server may not be reachable
        }
    }

    /**
     * Makes an authenticated GET request to the SonarQube API.
     *
     * @param path API endpoint path (e.g., "/api/rules/search?repositories=qualimetry-gherkin")
     * @return parsed JSON response body
     * @throws Exception if the request fails or returns a non-200 status
     */
    protected static JsonObject apiGet(String path) throws Exception {
        HttpRequest request = newGetRequest(path);
        HttpResponse<String> response = HTTP_CLIENT.send(request,
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "API GET " + path + " returned " + response.statusCode()
                            + ": " + response.body());
        }
        return GSON.fromJson(response.body(), JsonObject.class);
    }

    /**
     * Makes an authenticated POST request to the SonarQube API.
     *
     * @param path     API endpoint path
     * @param formBody URL-encoded form body
     * @return parsed JSON response body
     * @throws Exception if the request fails
     */
    protected static JsonObject apiPost(String path, String formBody) throws Exception {
        HttpRequest request = newPostRequest(path, formBody);
        HttpResponse<String> response = HTTP_CLIENT.send(request,
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "API POST " + path + " returned " + response.statusCode()
                            + ": " + response.body());
        }
        return GSON.fromJson(response.body(), JsonObject.class);
    }

    // ---- HTTP request builders ----

    private static HttpRequest newGetRequest(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(SONAR_URL + path))
                .timeout(REQUEST_TIMEOUT)
                .GET();
        addAuth(builder);
        return builder.build();
    }

    private static HttpRequest newPostRequest(String path, String formBody) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(SONAR_URL + path))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody));
        addAuth(builder);
        return builder.build();
    }

    /**
     * Adds Basic authentication header using the SonarQube token.
     * SonarQube token authentication uses the token as the username with an empty password.
     */
    private static void addAuth(HttpRequest.Builder builder) {
        if (SONAR_TOKEN != null && !SONAR_TOKEN.isBlank()) {
            String credentials = SONAR_TOKEN + ":";
            String encoded = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            builder.header("Authorization", "Basic " + encoded);
        }
    }

    /**
     * Returns the platform-appropriate sonar-scanner command name.
     */
    private static String getScannerCommand() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("win") ? "sonar-scanner.bat" : "sonar-scanner";
    }
}
