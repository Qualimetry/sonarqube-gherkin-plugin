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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.DataTableDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.testing.CheckVerifier;
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoEmptyExamplesCellsCheckTest {

    @Test
    void shouldNotRaiseIssueForPopulatedCells() {
        CheckVerifier.verifyNoIssues(
                new NoEmptyExamplesCellsCheck(),
                "checks/no-empty-examples-cells/compliant.feature");
    }

    @Test
    void shouldRaiseIssueForEmptyCell() {
        CheckVerifier.verify(
                new NoEmptyExamplesCellsCheck(),
                "checks/no-empty-examples-cells/noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueForNoExamples() {
        CheckVerifier.verifyNoIssues(
                new NoEmptyExamplesCellsCheck(),
                "checks/no-empty-examples-cells/no-examples.feature");
    }

    @Test
    void shouldRaiseIssueForRaggedRowMissingTrailingCells() {
        // The Cucumber parser rejects ragged tables in .feature sources, so
        // the ragged shape is built directly on the model.
        NoEmptyExamplesCellsCheck check = new NoEmptyExamplesCellsCheck();
        FeatureContext context = new FeatureContext(
                new FeatureFile(null, List.of(), "en", "ragged.feature"), null, "");
        check.setContext(context);

        ExamplesDefinition examples = new ExamplesDefinition(
                new TextPosition(8, 5), "Examples", "", "", List.of(),
                new DataTableDefinition(new TextPosition(9, 7), List.of(
                        List.of("product", "count"),
                        List.of("laptop"),
                        List.of("mouse", "8"))));

        check.visitExamples(examples);

        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).message())
                .isEqualTo("Examples table has an empty cell in data row 1, column \"count\".");
    }
}
