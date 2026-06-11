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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.BackgroundDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.DataTableDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.DocStringDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks that steps within a scenario or background are not duplicated.
 * <p>
 * Repeating the exact same step within a single scenario or background
 * is usually a mistake. Each step should be unique and contribute a
 * distinct piece of behaviour to the test. Steps with the same sentence
 * but different attached data tables or doc strings are not duplicates.
 */
@Rule(key = "no-duplicate-steps")
public class NoDuplicateStepsCheck extends BaseCheck {

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        checkForDuplicates(scenario.steps());
    }

    @Override
    public void visitBackground(BackgroundDefinition background) {
        checkForDuplicates(background.steps());
    }

    private void checkForDuplicates(List<StepDefinition> steps) {
        Set<String> seen = new HashSet<>();
        for (StepDefinition step : steps) {
            if (!seen.add(stepKey(step))) {
                addIssue(step.position(),
                        "Remove this duplicate step. The same step text appears earlier in this block.");
            }
        }
    }

    private static String stepKey(StepDefinition step) {
        return step.keywordType()
                + "\u0001" + step.text().trim()
                + "\u0001" + dataTableFingerprint(step.dataTable())
                + "\u0001" + docStringFingerprint(step.docString());
    }

    private static String dataTableFingerprint(DataTableDefinition dataTable) {
        if (dataTable == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (List<String> row : dataTable.rows()) {
            sb.append(String.join("\u001F", row)).append('\u001E');
        }
        return sb.toString();
    }

    private static String docStringFingerprint(DocStringDefinition docString) {
        return docString == null ? "" : docString.content();
    }
}
