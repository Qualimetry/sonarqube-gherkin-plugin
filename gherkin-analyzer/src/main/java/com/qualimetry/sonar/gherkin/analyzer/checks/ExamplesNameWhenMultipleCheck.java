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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that when a Scenario Outline has multiple Examples sections,
 * each section has a descriptive name.
 * <p>
 * When a Scenario Outline uses two or more Examples tables, unnamed
 * tables become indistinguishable in test reports. Naming each
 * Examples section clarifies the data variations being exercised.
 * This rule only triggers when there are 2+ Examples sections.
 */
@Rule(key = "examples-name-when-multiple")
public class ExamplesNameWhenMultipleCheck extends BaseCheck {

    @Override
    public void leaveScenario(ScenarioDefinition scenario) {
        if (scenario.examples().size() <= 1) {
            return;
        }

        for (ExamplesDefinition examples : scenario.examples()) {
            if (examples.name() == null || examples.name().isBlank()) {
                addIssue(examples.position(),
                        "Add a descriptive name to this Examples section; "
                                + "when multiple Examples sections exist, each should be named "
                                + "to distinguish them in test reports.");
            }
        }
    }
}
