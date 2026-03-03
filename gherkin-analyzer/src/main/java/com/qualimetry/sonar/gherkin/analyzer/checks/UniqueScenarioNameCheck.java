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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import com.qualimetry.sonar.gherkin.analyzer.visitor.CrossFileIssue;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checks that Scenario names are unique within and across features.
 * <p>
 * Duplicate Scenario names make test reports ambiguous and make it
 * difficult to identify which specific test case failed or passed.
 * <p>
 * This is a cross-file check that returns structured {@link CrossFileIssue}
 * results from {@link #afterAllFiles()}.
 */
@Rule(key = "unique-scenario-name")
public class UniqueScenarioNameCheck extends BaseCheck {

    private final Map<String, List<NameOccurrence>> nameOccurrences = new HashMap<>();

    private record NameOccurrence(String uri, TextPosition position) {}

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        if (scenario.name() != null && !scenario.name().isBlank()) {
            String uri = getContext().getFeatureFile().uri();
            nameOccurrences.computeIfAbsent(scenario.name(), k -> new ArrayList<>())
                    .add(new NameOccurrence(uri, scenario.position()));
        }
    }

    /**
     * Called after all files have been processed. Returns structured
     * cross-file issues for duplicate Scenario names.
     *
     * @return list of cross-file issues with correct file URIs
     */
    public List<CrossFileIssue> afterAllFiles() {
        List<CrossFileIssue> issues = new ArrayList<>();
        for (Map.Entry<String, List<NameOccurrence>> entry : nameOccurrences.entrySet()) {
            List<NameOccurrence> occurrences = entry.getValue();
            if (occurrences.size() > 1) {
                for (int i = 1; i < occurrences.size(); i++) {
                    NameOccurrence occ = occurrences.get(i);
                    issues.add(new CrossFileIssue(
                            getRuleKey(),
                            occ.uri(),
                            occ.position().line(),
                            "Rename this Scenario. The name \"" + entry.getKey()
                                    + "\" is already used in " + occurrences.get(0).uri() + "."));
                }
            }
        }
        return issues;
    }
}
