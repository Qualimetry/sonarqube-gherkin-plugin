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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that a blank line precedes each Scenario or Scenario Outline keyword.
 * <p>
 * A blank separator line before the scenario keyword improves readability
 * by visually separating scenarios from the preceding content. This follows
 * the same pattern as {@link ExamplesSeparatorLineCheck} for Examples sections.
 * <p>
 * Uses the raw content approach ({@code getContext().getRawContent()}) to
 * inspect the line before the scenario keyword.
 */
@Rule(key = "blank-line-before-scenario")
public class BlankLineBeforeScenarioCheck extends BaseCheck {

    private String[] lines;

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent != null) {
            lines = rawContent.split("\\r?\\n", -1);
        } else {
            lines = null;
        }
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        if (lines == null) {
            return;
        }

        int scenarioLine = scenario.position().line(); // 1-based

        // If tags are present, the check should look at the line before the first tag
        // since tags visually belong to the scenario
        int effectiveLine = scenarioLine;
        if (!scenario.tags().isEmpty()) {
            int firstTagLine = scenario.tags().get(0).position().line();
            if (firstTagLine < effectiveLine) {
                effectiveLine = firstTagLine;
            }
        }

        if (effectiveLine <= 1) {
            return; // first line in file, nothing to check
        }

        // Check the line before the scenario (or its first tag) - 0-based index
        int prevIndex = effectiveLine - 2;
        if (prevIndex >= 0 && prevIndex < lines.length) {
            String prevLine = lines[prevIndex].trim();
            if (!prevLine.isEmpty()) {
                addIssue(scenario.position(),
                        "Add a blank line before this Scenario.");
            }
        }
    }
}
