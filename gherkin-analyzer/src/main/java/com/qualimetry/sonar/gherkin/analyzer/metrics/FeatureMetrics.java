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
package com.qualimetry.sonar.gherkin.analyzer.metrics;

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;

/**
 * Computes source code metrics from a parsed {@link FeatureFile}.
 * <p>
 * Produces five metrics that map to the standard SonarQube measures:
 * <ul>
 *   <li><b>ncloc</b> - non-comment, non-blank lines of code</li>
 *   <li><b>commentLines</b> - lines containing comments</li>
 *   <li><b>statements</b> - total number of steps (Given/When/Then/And/But)</li>
 *   <li><b>functions</b> - total number of Scenarios and Scenario Outlines</li>
 *   <li><b>classes</b> - 1 if a Feature definition exists, 0 otherwise</li>
 * </ul>
 */
public final class FeatureMetrics {

    private FeatureMetrics() {
        // utility class
    }

    /**
     * Computes metrics from the parsed tree and raw file content.
     *
     * @param featureFile the parsed feature file tree
     * @param rawContent  the raw file content as a string (required for line-based metrics)
     * @return the computed metric values
     */
    public static MetricResult compute(FeatureFile featureFile, String rawContent) {
        int ncloc = 0;
        int commentLines = 0;

        if (rawContent != null && !rawContent.isEmpty()) {
            String[] lines = rawContent.split("\\r?\\n", -1);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    // blank line - neither ncloc nor comment
                } else if (trimmed.startsWith("#")) {
                    commentLines++;
                } else {
                    ncloc++;
                }
            }
        }

        int statements = 0;
        int functions = 0;
        int classes = 0;

        if (featureFile.feature() != null) {
            FeatureDefinition feature = featureFile.feature();
            classes = 1;

            // Count steps in feature-level background
            if (feature.background() != null) {
                statements += feature.background().steps().size();
            }

            // Count top-level scenarios and their steps
            for (ScenarioDefinition scenario : feature.scenarios()) {
                functions++;
                statements += scenario.steps().size();
            }

            // Count scenarios and steps inside Rule blocks
            for (RuleDefinition rule : feature.rules()) {
                if (rule.background() != null) {
                    statements += rule.background().steps().size();
                }
                for (ScenarioDefinition scenario : rule.scenarios()) {
                    functions++;
                    statements += scenario.steps().size();
                }
            }
        }

        return new MetricResult(ncloc, commentLines, statements, functions, classes);
    }

    /**
     * Holds the computed metric values for a feature file.
     *
     * @param ncloc        non-comment, non-blank lines of code
     * @param commentLines number of comment lines
     * @param statements   total number of steps
     * @param functions    total number of scenarios
     * @param classes      1 if a Feature exists, 0 otherwise
     */
    public record MetricResult(int ncloc, int commentLines, int statements, int functions, int classes) {
    }
}
