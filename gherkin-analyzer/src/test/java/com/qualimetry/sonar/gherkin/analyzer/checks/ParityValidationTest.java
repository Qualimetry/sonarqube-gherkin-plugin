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

import org.junit.jupiter.api.Test;
import org.sonar.check.Rule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Structural test that verifies completeness of the rule suite.
 * <p>
 * Ensures all 83 rules are registered, no rule key collides with reserved keys,
 * every rule has an HTML description, and every rule has test fixture files.
 */
class ParityValidationTest {

    /**
     * Load reserved rule keys from external file (used for collision-avoidance check).
     */
    private static List<String> loadReservedKeys() {
        try (var reader = new BufferedReader(new InputStreamReader(
                ParityValidationTest.class.getResourceAsStream(
                        "/reserved-rule-keys.txt"), StandardCharsets.UTF_8))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Cannot load reserved-rule-keys.txt", e);
        }
    }

    @Test
    void allRulesRegistered() {
        assertThat(CheckList.getAllChecks()).hasSize(83);
    }

    @Test
    void noRuleKeyCollidesWithReservedKeys() {
        List<String> reservedKeys = loadReservedKeys();
        List<String> newKeys = CheckList.getAllChecks().stream()
                .map(c -> c.getAnnotation(Rule.class).key())
                .toList();
        for (String newKey : newKeys) {
            assertThat(reservedKeys)
                    .as("Rule key '%s' collides with a reserved key", newKey)
                    .doesNotContain(newKey);
        }
    }

    @Test
    void everyRuleHasHtmlDescription() {
        for (Class<?> check : CheckList.getAllChecks()) {
            String key = check.getAnnotation(Rule.class).key();
            String path = "/com/qualimetry/sonar/gherkin/analyzer/checks/" + key + ".html";
            assertThat(getClass().getResourceAsStream(path))
                    .as("Missing HTML description for rule: %s", key)
                    .isNotNull();
        }
    }

    @Test
    void everyRuleHasTestFixtures() {
        for (Class<?> check : CheckList.getAllChecks()) {
            String key = check.getAnnotation(Rule.class).key();
            Path dir = Path.of("src/test/resources/checks/" + key);
            assertThat(dir.toFile().isDirectory())
                    .as("Missing test fixture directory for rule: %s", key)
                    .isTrue();
        }
    }

    @Test
    void defaultProfileHasExpectedSize() {
        // 53 rules in default profile (includes 75, 80, 83).
        assertThat(CheckList.getDefaultRuleKeys()).hasSize(53);
    }

    @Test
    void allDefaultKeysAreRegistered() {
        List<String> allKeys = CheckList.getAllChecks().stream()
                .map(c -> c.getAnnotation(Rule.class).key())
                .toList();
        for (String defaultKey : CheckList.getDefaultRuleKeys()) {
            assertThat(allKeys)
                    .as("Default key '%s' is not in getAllChecks()", defaultKey)
                    .contains(defaultKey);
        }
    }

    @Test
    void allRuleKeysAreUnique() {
        List<String> allKeys = CheckList.getAllChecks().stream()
                .map(c -> c.getAnnotation(Rule.class).key())
                .toList();
        assertThat(allKeys).doesNotHaveDuplicates();
    }
}
