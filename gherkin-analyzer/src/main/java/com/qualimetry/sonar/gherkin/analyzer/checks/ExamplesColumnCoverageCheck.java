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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks that {@code Examples} tables include columns for all variables
 * referenced in the {@code Scenario Outline} steps.
 * <p>
 * When a step references a variable like {@code <username>} but the Examples
 * table does not have a matching column header, the scenario outline will
 * produce incorrect test data and likely fail silently or produce misleading
 * results.
 */
@Rule(key = "examples-column-coverage")
public class ExamplesColumnCoverageCheck extends BaseCheck {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("<([^>]+)>");

    private ScenarioDefinition currentScenario;

    @Override
    public void visitFeatureFile(FeatureFile file) {
        currentScenario = null;
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        currentScenario = scenario;
    }

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        if (currentScenario == null || !currentScenario.isOutline()) {
            return;
        }
        if (examples.table() == null || examples.table().rows().isEmpty()) {
            return;
        }

        // Header columns are in row 0
        Set<String> headerColumns = new HashSet<>(examples.table().rows().get(0));

        // Collect all variables referenced in step text
        Set<String> referencedVariables = new LinkedHashSet<>();
        for (StepDefinition step : currentScenario.steps()) {
            Matcher matcher = VARIABLE_PATTERN.matcher(step.text());
            while (matcher.find()) {
                referencedVariables.add(matcher.group(1));
            }
        }

        // Report variables missing from the Examples header
        for (String variable : referencedVariables) {
            if (!headerColumns.contains(variable)) {
                addIssue(examples.position(),
                        "Add a \"" + variable + "\" column to this Examples table.");
            }
        }
    }
}
