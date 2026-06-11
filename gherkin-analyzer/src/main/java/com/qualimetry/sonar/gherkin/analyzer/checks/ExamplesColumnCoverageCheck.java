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
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks that {@code Examples} tables include columns for all variables
 * referenced in the {@code Scenario Outline} steps.
 * <p>
 * Variables are collected from step sentence text, doc string content,
 * and data table cells. When a step references a variable like
 * {@code <username>} but the Examples table does not have a matching
 * column header, the scenario outline will produce incorrect test data
 * and likely fail silently or produce misleading results.
 */
@Rule(key = "examples-column-coverage")
public class ExamplesColumnCoverageCheck extends BaseCheck {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("<([^>]+)>");

    private ScenarioDefinition currentScenario;
    private Set<String> referencedVariables = new LinkedHashSet<>();

    @Override
    public void visitFeatureFile(FeatureFile file) {
        currentScenario = null;
        referencedVariables = new LinkedHashSet<>();
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        currentScenario = scenario;
        referencedVariables = collectReferencedVariables(scenario);
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

        // Report variables missing from the Examples header
        for (String variable : referencedVariables) {
            if (!headerColumns.contains(variable)) {
                addIssue(examples.position(),
                        "Add a \"" + variable + "\" column to this Examples table.");
            }
        }
    }

    private static Set<String> collectReferencedVariables(ScenarioDefinition scenario) {
        Set<String> variables = new LinkedHashSet<>();
        for (StepDefinition step : scenario.steps()) {
            collectVariables(step.text(), variables);
            if (step.docString() != null) {
                collectVariables(step.docString().content(), variables);
            }
            if (step.dataTable() != null) {
                for (List<String> row : step.dataTable().rows()) {
                    for (String cell : row) {
                        collectVariables(cell, variables);
                    }
                }
            }
        }
        return variables;
    }

    private static void collectVariables(String text, Set<String> variables) {
        Matcher matcher = VARIABLE_PATTERN.matcher(text);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
    }
}
