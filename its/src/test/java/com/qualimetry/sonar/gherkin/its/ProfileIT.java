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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests verifying the Gherkin Analyzer quality profile
 * is correctly registered on the SonarQube server.
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
class ProfileIT extends IntegrationTestBase {

    private String profileKey;

    @BeforeAll
    void setUp() throws Exception {
        assumeServerAvailable();
        profileKey = findProfileKey();
    }

    @Test
    void defaultProfileExists() {
        assertThat(profileKey)
                .as("The '%s' quality profile should exist for language '%s'",
                        PROFILE_NAME, LANGUAGE_KEY)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void defaultProfileIsDefault() throws Exception {
        JsonObject response = apiGet(
                "/api/qualityprofiles/search?language=" + LANGUAGE_KEY);
        JsonArray profiles = response.getAsJsonArray("profiles");
        assertThat(profiles).isNotNull();

        boolean isDefault = false;
        for (JsonElement element : profiles) {
            JsonObject profile = element.getAsJsonObject();
            if (PROFILE_NAME.equals(profile.get("name").getAsString())) {
                isDefault = profile.has("isDefault")
                        && profile.get("isDefault").getAsBoolean();
                break;
            }
        }
        assertThat(isDefault)
                .as("The '%s' quality profile should be the default", PROFILE_NAME)
                .isTrue();
    }

    @Test
    void defaultProfileHas53ActiveRules() throws Exception {
        assertThat(profileKey).isNotNull();

        String encodedKey = URLEncoder.encode(profileKey, StandardCharsets.UTF_8);
        JsonObject response = apiGet(
                "/api/rules/search?activation=true&qprofile=" + encodedKey
                        + "&ps=1&p=1");
        int activeRules = response.get("total").getAsInt();
        assertThat(activeRules)
                .as("The default profile should have %d active rules",
                        EXPECTED_DEFAULT_ACTIVE_RULES)
                .isEqualTo(EXPECTED_DEFAULT_ACTIVE_RULES);
    }

    // ---- Helpers ----

    /**
     * Finds the profile key for the "Qualimetry Gherkin" profile.
     *
     * @return the profile key, or null if not found
     */
    private String findProfileKey() throws Exception {
        JsonObject response = apiGet(
                "/api/qualityprofiles/search?language=" + LANGUAGE_KEY);
        JsonArray profiles = response.getAsJsonArray("profiles");
        if (profiles == null) {
            return null;
        }
        for (JsonElement element : profiles) {
            JsonObject profile = element.getAsJsonObject();
            if (PROFILE_NAME.equals(profile.get("name").getAsString())) {
                return profile.get("key").getAsString();
            }
        }
        return null;
    }
}
