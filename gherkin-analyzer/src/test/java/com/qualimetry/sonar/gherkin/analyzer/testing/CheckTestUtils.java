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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility methods for rule check tests and evidence report generation.
 */
public final class CheckTestUtils {

    /** Base classpath path for rule HTML descriptions. */
    private static final String DESCRIPTION_RESOURCE_BASE =
            "/com/qualimetry/sonar/gherkin/analyzer/checks/";

    private CheckTestUtils() {
        // utility class
    }

    /**
     * Builds the classpath resource path for a rule's test fixture file.
     * <p>
     * Convention: fixture files are stored under
     * {@code src/test/resources/checks/<ruleKey>/<fileName>}.
     *
     * @param ruleKey  the rule key (e.g., {@code "feature-file-required"})
     * @param fileName the fixture file name (e.g., {@code "noncompliant.feature"})
     * @return the classpath resource path
     */
    public static String fixturePathForRule(String ruleKey, String fileName) {
        return "checks/" + ruleKey + "/" + fileName;
    }

    /**
     * Discovers all {@code .feature} fixture files for a given rule key by
     * scanning the classpath directory {@code checks/<ruleKey>/}.
     * <p>
     * Returns classpath-relative paths suitable for passing to
     * {@link CheckVerifier#collectEvidence} or {@link CheckVerifier#verify}.
     *
     * @param ruleKey the rule key (e.g., {@code "step-required"})
     * @return a sorted list of classpath paths, or an empty list if the
     *         directory does not exist or contains no {@code .feature} files
     */
    public static List<String> discoverFixturesForRule(String ruleKey) {
        try {
            java.net.URL dirUrl = CheckTestUtils.class.getResource("/checks/" + ruleKey);
            if (dirUrl == null) {
                return Collections.emptyList();
            }
            Path dir = Path.of(dirUrl.toURI());
            try (Stream<Path> files = Files.list(dir)) {
                return files
                        .filter(p -> p.getFileName().toString().endsWith(".feature"))
                        .sorted()
                        .map(p -> "checks/" + ruleKey + "/" + p.getFileName().toString())
                        .toList();
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Reads the HTML description for a rule from the classpath.
     *
     * @param ruleKey the rule key (e.g., {@code "step-required"})
     * @return the HTML description content, or {@code null} if not found
     */
    public static String readRuleDescriptionHtml(String ruleKey) {
        String resourcePath = DESCRIPTION_RESOURCE_BASE + ruleKey + ".html";
        try (InputStream is = CheckTestUtils.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Reads a {@code .properties} sidecar file for a fixture if one exists.
     * The sidecar file must have the same name as the fixture but with
     * a {@code .properties} extension instead of {@code .feature}.
     * <p>
     * For example, for fixture {@code checks/scenario-count-limit/custom-limit-noncompliant.feature},
     * the sidecar file would be {@code checks/scenario-count-limit/custom-limit-noncompliant.properties}.
     * <p>
     * The properties map {@code @RuleProperty} key names to their values.
     *
     * @param fixturePath the classpath resource path of the fixture file
     * @return a map of property keys to values, or an empty map if no sidecar file exists
     */
    public static Map<String, String> readFixtureConfig(String fixturePath) {
        String propsPath = fixturePath.replace(".feature", ".properties");
        String resourcePath = propsPath.startsWith("/") ? propsPath : "/" + propsPath;
        try (InputStream is = CheckTestUtils.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                return Map.of();
            }
            Properties props = new Properties();
            props.load(is);
            Map<String, String> result = new HashMap<>();
            for (String key : props.stringPropertyNames()) {
                result.put(key, props.getProperty(key));
            }
            return result;
        } catch (Exception e) {
            return Map.of();
        }
    }

    /**
     * Converts a kebab-case rule key to a human-readable Title Case display name.
     * For example, {@code "step-count-limit"} becomes {@code "Step Count Limit"}.
     *
     * @param ruleKey the rule key in kebab-case
     * @return the title-case display name
     */
    public static String toDisplayName(String ruleKey) {
        if (ruleKey == null || ruleKey.isEmpty()) {
            return ruleKey;
        }
        String[] parts = ruleKey.split("-");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            String part = parts[i];
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    sb.append(part.substring(1));
                }
            }
        }
        return sb.toString();
    }
}
