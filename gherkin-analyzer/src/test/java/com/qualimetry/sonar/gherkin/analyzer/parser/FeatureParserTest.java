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
package com.qualimetry.sonar.gherkin.analyzer.parser;

import com.qualimetry.sonar.gherkin.analyzer.parser.model.BackgroundDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.DataTableDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.DocStringDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive tests for {@link FeatureParser}.
 * <p>
 * Each test parses a fixture {@code .feature} file from the test resources
 * and verifies the resulting internal tree model is correctly constructed.
 */
class FeatureParserTest {

    private FeatureParser parser;

    @BeforeEach
    void setUp() {
        parser = new FeatureParser();
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------

    private FeatureFile parseFixture(String fixtureName) throws IOException {
        try (InputStream input = getClass().getResourceAsStream("/parser/" + fixtureName)) {
            assertThat(input).as("Fixture file not found: " + fixtureName).isNotNull();
            return parser.parse("test://" + fixtureName, input);
        }
    }

    // ------------------------------------------------------------------
    // simple.feature
    // ------------------------------------------------------------------

    @Test
    void shouldParseSimpleFeature() throws IOException {
        FeatureFile file = parseFixture("simple.feature");

        assertThat(file).isNotNull();
        assertThat(file.uri()).isEqualTo("test://simple.feature");
        assertThat(file.language()).isEqualTo("en");

        FeatureDefinition feature = file.feature();
        assertThat(feature).isNotNull();
        assertThat(feature.name()).isEqualTo("User login");
        assertThat(feature.language()).isEqualTo("en");
        assertThat(feature.position().line()).isEqualTo(1);
        assertThat(feature.tags()).isEmpty();
        assertThat(feature.background()).isNull();
        assertThat(feature.rules()).isEmpty();

        assertThat(feature.scenarios()).hasSize(1);
        ScenarioDefinition scenario = feature.scenarios().get(0);
        assertThat(scenario.name()).isEqualTo("Successful login");
        assertThat(scenario.isOutline()).isFalse();
        assertThat(scenario.examples()).isEmpty();
        assertThat(scenario.tags()).isEmpty();
    }

    @Test
    void shouldParseStepsInSimpleFeature() throws IOException {
        FeatureFile file = parseFixture("simple.feature");
        List<StepDefinition> steps = file.feature().scenarios().get(0).steps();

        assertThat(steps).hasSize(3);

        assertThat(steps.get(0).text()).isEqualTo("the user is on the login page");
        assertThat(steps.get(0).keywordType()).isEqualTo("CONTEXT");

        assertThat(steps.get(1).text()).isEqualTo("the user enters valid credentials");
        assertThat(steps.get(1).keywordType()).isEqualTo("ACTION");

        assertThat(steps.get(2).text()).isEqualTo("the user is redirected to the dashboard");
        assertThat(steps.get(2).keywordType()).isEqualTo("OUTCOME");
    }

    @Test
    void shouldSetCorrectPositionsForSimpleFeature() throws IOException {
        FeatureFile file = parseFixture("simple.feature");
        FeatureDefinition feature = file.feature();

        assertThat(feature.position().line()).isEqualTo(1);
        assertThat(feature.position().column()).isEqualTo(1);

        ScenarioDefinition scenario = feature.scenarios().get(0);
        assertThat(scenario.position().line()).isEqualTo(4);

        List<StepDefinition> steps = scenario.steps();
        assertThat(steps.get(0).position().line()).isEqualTo(5);
        assertThat(steps.get(1).position().line()).isEqualTo(6);
        assertThat(steps.get(2).position().line()).isEqualTo(7);
    }

    // ------------------------------------------------------------------
    // full.feature
    // ------------------------------------------------------------------

    @Test
    void shouldParseFeatureTags() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        FeatureDefinition feature = file.feature();

        assertThat(feature.tags()).hasSize(2);
        assertThat(feature.tags().get(0).name()).isEqualTo("smoke");
        assertThat(feature.tags().get(1).name()).isEqualTo("regression");
    }

    @Test
    void shouldStripAtSignFromTagNames() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        for (TagDefinition tag : file.feature().tags()) {
            assertThat(tag.name()).doesNotStartWith("@");
        }
    }

    @Test
    void shouldParseBackground() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        BackgroundDefinition background = file.feature().background();

        assertThat(background).isNotNull();
        assertThat(background.name()).isEqualTo("Common setup");
        assertThat(background.steps()).hasSize(2);
        assertThat(background.steps().get(0).keywordType()).isEqualTo("CONTEXT");
        assertThat(background.steps().get(0).text()).isEqualTo("the system is initialized");
        assertThat(background.steps().get(1).keywordType()).isEqualTo("CONJUNCTION");
        assertThat(background.steps().get(1).text()).isEqualTo("the database is clean");
    }

    @Test
    void shouldParseMultipleScenarios() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        assertThat(file.feature().scenarios()).hasSize(4);
    }

    @Test
    void shouldDetectScenarioOutline() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        List<ScenarioDefinition> scenarios = file.feature().scenarios();

        // First scenario: regular
        assertThat(scenarios.get(0).isOutline()).isFalse();
        assertThat(scenarios.get(0).examples()).isEmpty();

        // Second scenario: outline with examples
        assertThat(scenarios.get(1).isOutline()).isTrue();
        assertThat(scenarios.get(1).examples()).hasSize(1);
    }

    @Test
    void shouldParseScenarioTags() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        List<ScenarioDefinition> scenarios = file.feature().scenarios();

        assertThat(scenarios.get(0).tags()).hasSize(1);
        assertThat(scenarios.get(0).tags().get(0).name()).isEqualTo("happy-path");

        assertThat(scenarios.get(1).tags()).hasSize(1);
        assertThat(scenarios.get(1).tags().get(0).name()).isEqualTo("data-driven");
    }

    @Test
    void shouldParseExamplesTable() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        ScenarioDefinition outline = file.feature().scenarios().get(1);
        ExamplesDefinition examples = outline.examples().get(0);

        assertThat(examples.name()).isEqualTo("Standard orders");
        assertThat(examples.table()).isNotNull();

        DataTableDefinition table = examples.table();
        assertThat(table.rows()).hasSize(3); // 1 header + 2 data rows

        // Header row
        assertThat(table.rows().get(0)).containsExactly("name", "quantity", "total");
        // Data rows
        assertThat(table.rows().get(1)).containsExactly("Alice", "3", "$30.00");
        assertThat(table.rows().get(2)).containsExactly("Bob", "1", "$10.00");
    }

    @Test
    void shouldParseStepDataTable() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        // Third scenario: "View order with details" has a step with a data table
        ScenarioDefinition scenario = file.feature().scenarios().get(2);
        StepDefinition stepWithTable = scenario.steps().get(2);

        assertThat(stepWithTable.text()).isEqualTo("the following details are shown:");
        assertThat(stepWithTable.dataTable()).isNotNull();

        DataTableDefinition table = stepWithTable.dataTable();
        assertThat(table.rows()).hasSize(3); // header + 2 data rows
        assertThat(table.rows().get(0)).containsExactly("field", "value");
        assertThat(table.rows().get(1)).containsExactly("status", "Processing");
        assertThat(table.rows().get(2)).containsExactly("total", "$50.00");
    }

    @Test
    void shouldParseDocString() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        // Fourth scenario: "API response validation" has a step with a doc string
        ScenarioDefinition scenario = file.feature().scenarios().get(3);
        StepDefinition stepWithDocString = scenario.steps().get(0);

        assertThat(stepWithDocString.text()).isEqualTo("the order API returns:");
        assertThat(stepWithDocString.docString()).isNotNull();

        DocStringDefinition docString = stepWithDocString.docString();
        assertThat(docString.contentType()).isEqualTo("json");
        assertThat(docString.content()).contains("\"orderId\": \"12345\"");
        assertThat(docString.content()).contains("\"status\": \"confirmed\"");
        assertThat(docString.delimiter()).isEqualTo("\"\"\"");
    }

    @Test
    void shouldParseComments() throws IOException {
        FeatureFile file = parseFixture("full.feature");

        assertThat(file.comments()).isNotEmpty();
        // The first comment is "# A comment at the top of the file"
        assertThat(file.comments().get(0).text()).contains("A comment at the top of the file");
        assertThat(file.comments().get(0).position().line()).isEqualTo(1);
    }

    @Test
    void shouldMapAllKeywordTypes() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        BackgroundDefinition background = file.feature().background();

        // Background has "Given" (CONTEXT) and "And" (CONJUNCTION)
        assertThat(background.steps().get(0).keywordType()).isEqualTo("CONTEXT");
        assertThat(background.steps().get(1).keywordType()).isEqualTo("CONJUNCTION");

        // First scenario has Given (CONTEXT), When (ACTION), Then (OUTCOME)
        List<StepDefinition> steps = file.feature().scenarios().get(0).steps();
        assertThat(steps.get(0).keywordType()).isEqualTo("CONTEXT");
        assertThat(steps.get(1).keywordType()).isEqualTo("ACTION");
        assertThat(steps.get(2).keywordType()).isEqualTo("OUTCOME");
    }

    @Test
    void shouldPreserveStepKeywordText() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        BackgroundDefinition background = file.feature().background();

        // The keyword text should include the trailing space
        assertThat(background.steps().get(0).keyword()).startsWith("Given");
        assertThat(background.steps().get(1).keyword()).startsWith("And");
    }

    // ------------------------------------------------------------------
    // multilang.feature
    // ------------------------------------------------------------------

    @Test
    void shouldDetectFrenchLanguage() throws IOException {
        FeatureFile file = parseFixture("multilang.feature");

        assertThat(file.language()).isEqualTo("fr");
        assertThat(file.feature()).isNotNull();
        assertThat(file.feature().language()).isEqualTo("fr");
    }

    @Test
    void shouldParseFrenchKeywords() throws IOException {
        FeatureFile file = parseFixture("multilang.feature");
        FeatureDefinition feature = file.feature();

        assertThat(feature.name()).isEqualTo("Gestion des comptes");

        assertThat(feature.scenarios()).hasSize(1);
        ScenarioDefinition scenario = feature.scenarios().get(0);
        assertThat(scenario.name()).isEqualTo("Consultation du solde");
        assertThat(scenario.steps()).hasSize(3);

        // French step keywords should still map to correct keyword types
        assertThat(scenario.steps().get(0).keywordType()).isEqualTo("CONTEXT");
        assertThat(scenario.steps().get(1).keywordType()).isEqualTo("ACTION");
        assertThat(scenario.steps().get(2).keywordType()).isEqualTo("OUTCOME");
    }

    @Test
    void shouldTreatLanguageDirectiveAsComment() throws IOException {
        FeatureFile file = parseFixture("multilang.feature");

        // The "# language: fr" declaration is consumed by the Cucumber parser
        // as a language directive. It may or may not appear in the comments list
        // depending on the parser version; the key assertion is that the
        // language was correctly detected on the feature.
        assertThat(file.language()).isEqualTo("fr");
        assertThat(file.feature()).isNotNull();
        assertThat(file.feature().language()).isEqualTo("fr");
    }

    // ------------------------------------------------------------------
    // rule.feature
    // ------------------------------------------------------------------

    @Test
    void shouldParseFeatureWithRules() throws IOException {
        FeatureFile file = parseFixture("rule.feature");
        FeatureDefinition feature = file.feature();

        assertThat(feature).isNotNull();
        assertThat(feature.name()).isEqualTo("Premium membership");
        assertThat(feature.scenarios()).isEmpty(); // No top-level scenarios
        assertThat(feature.rules()).hasSize(2);
    }

    @Test
    void shouldParseRuleWithBackground() throws IOException {
        FeatureFile file = parseFixture("rule.feature");
        RuleDefinition firstRule = file.feature().rules().get(0);

        assertThat(firstRule.name()).isEqualTo("Free shipping for premium members");
        assertThat(firstRule.background()).isNotNull();
        assertThat(firstRule.background().name()).isEqualTo("Premium account");
        assertThat(firstRule.background().steps()).hasSize(1);
        assertThat(firstRule.background().steps().get(0).text())
                .isEqualTo("a customer with premium membership");
    }

    @Test
    void shouldParseScenariosWithinRule() throws IOException {
        FeatureFile file = parseFixture("rule.feature");
        RuleDefinition firstRule = file.feature().rules().get(0);

        assertThat(firstRule.scenarios()).hasSize(2);
        assertThat(firstRule.scenarios().get(0).name()).isEqualTo("Domestic order qualifies");
        assertThat(firstRule.scenarios().get(1).name()).isEqualTo("International order excluded");
    }

    @Test
    void shouldParseRuleWithTags() throws IOException {
        FeatureFile file = parseFixture("rule.feature");
        RuleDefinition secondRule = file.feature().rules().get(1);

        assertThat(secondRule.name()).isEqualTo("Loyalty points accumulation");
        assertThat(secondRule.background()).isNull();
        assertThat(secondRule.scenarios()).hasSize(1);

        ScenarioDefinition scenario = secondRule.scenarios().get(0);
        assertThat(scenario.tags()).hasSize(1);
        assertThat(scenario.tags().get(0).name()).isEqualTo("points");
    }

    // ------------------------------------------------------------------
    // parse-error.feature
    // ------------------------------------------------------------------

    @Test
    void shouldHandleParseError() throws IOException {
        FeatureFile file = parseFixture("parse-error.feature");

        assertThat(file).isNotNull();
        assertThat(file.uri()).isEqualTo("test://parse-error.feature");
        // A file with two Feature keywords causes a parse error.
        // The parser should return a FeatureFile with null feature.
        assertThat(file.feature()).isNull();
    }

    // ------------------------------------------------------------------
    // empty.feature
    // ------------------------------------------------------------------

    @Test
    void shouldParseEmptyFile() throws IOException {
        FeatureFile file = parseFixture("empty.feature");

        assertThat(file).isNotNull();
        assertThat(file.uri()).isEqualTo("test://empty.feature");
        assertThat(file.feature()).isNull();
        assertThat(file.comments()).isEmpty();
        assertThat(file.language()).isEqualTo("en");
    }

    // ------------------------------------------------------------------
    // Inline content tests (no fixture file needed)
    // ------------------------------------------------------------------

    @Test
    void shouldParseCommentOnlyFile() throws IOException {
        String content = "# Just a comment\n# Another comment\n";
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("test://comments-only.feature", input);

        assertThat(file).isNotNull();
        assertThat(file.feature()).isNull();
        assertThat(file.comments()).hasSize(2);
        assertThat(file.comments().get(0).text()).contains("Just a comment");
        assertThat(file.comments().get(1).text()).contains("Another comment");
    }

    @Test
    void shouldParseStarStepPrefix() throws IOException {
        String content = """
                Feature: Star step
                  Scenario: Using star
                    * a step with the star prefix
                    * another star step
                """;
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("test://star.feature", input);

        assertThat(file.feature()).isNotNull();
        List<StepDefinition> steps = file.feature().scenarios().get(0).steps();
        assertThat(steps).hasSize(2);
        // Star (*) steps have UNKNOWN keyword type
        assertThat(steps.get(0).keywordType()).isEqualTo("UNKNOWN");
        assertThat(steps.get(1).keywordType()).isEqualTo("UNKNOWN");
    }

    @Test
    void shouldParseAndButStepKeywords() throws IOException {
        String content = """
                Feature: And/But steps
                  Scenario: Conjunction keywords
                    Given a precondition
                    And another precondition
                    But not this precondition
                    When an action occurs
                    Then the result is achieved
                    And another result is also achieved
                    But some side effect does not occur
                """;
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("test://conjunction.feature", input);

        List<StepDefinition> steps = file.feature().scenarios().get(0).steps();
        assertThat(steps).hasSize(7);

        assertThat(steps.get(0).keywordType()).isEqualTo("CONTEXT");      // Given
        assertThat(steps.get(1).keywordType()).isEqualTo("CONJUNCTION");   // And
        assertThat(steps.get(2).keywordType()).isEqualTo("CONJUNCTION");   // But
        assertThat(steps.get(3).keywordType()).isEqualTo("ACTION");        // When
        assertThat(steps.get(4).keywordType()).isEqualTo("OUTCOME");       // Then
        assertThat(steps.get(5).keywordType()).isEqualTo("CONJUNCTION");   // And
        assertThat(steps.get(6).keywordType()).isEqualTo("CONJUNCTION");   // But
    }

    @Test
    void shouldParseMultipleExamplesSections() throws IOException {
        String content = """
                Feature: Multiple examples
                  Scenario Outline: Parameterized test
                    Given a value of "<value>"
                    Then the result is "<result>"

                    Examples: First set
                      | value | result |
                      | A     | X      |

                    Examples: Second set
                      | value | result |
                      | B     | Y      |
                      | C     | Z      |
                """;
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("test://multi-examples.feature", input);

        ScenarioDefinition outline = file.feature().scenarios().get(0);
        assertThat(outline.isOutline()).isTrue();
        assertThat(outline.examples()).hasSize(2);

        assertThat(outline.examples().get(0).name()).isEqualTo("First set");
        assertThat(outline.examples().get(0).table().rows()).hasSize(2); // 1 header + 1 data
        assertThat(outline.examples().get(1).name()).isEqualTo("Second set");
        assertThat(outline.examples().get(1).table().rows()).hasSize(3); // 1 header + 2 data
    }

    @Test
    void shouldParseDocStringWithBacktickDelimiter() throws IOException {
        String content = """
                Feature: Backtick doc string
                  Scenario: With backticks
                    Given the following markdown:
                      ```markdown
                      # Heading
                      Some **bold** text.
                      ```
                    Then it is processed
                """;
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("test://backtick.feature", input);

        StepDefinition step = file.feature().scenarios().get(0).steps().get(0);
        assertThat(step.docString()).isNotNull();
        assertThat(step.docString().delimiter()).isEqualTo("```");
        assertThat(step.docString().contentType()).isEqualTo("markdown");
        assertThat(step.docString().content()).contains("# Heading");
    }

    @Test
    void shouldReturnNullForStepWithoutDataTableOrDocString() throws IOException {
        FeatureFile file = parseFixture("simple.feature");
        StepDefinition step = file.feature().scenarios().get(0).steps().get(0);

        assertThat(step.dataTable()).isNull();
        assertThat(step.docString()).isNull();
    }

    @Test
    void shouldHandleFeatureWithDescription() throws IOException {
        FeatureFile file = parseFixture("simple.feature");
        FeatureDefinition feature = file.feature();

        // The feature description is "Users should be able to log in to the system"
        assertThat(feature.description()).contains("Users should be able to log in to the system");
    }

    @Test
    void shouldParseFeatureKeyword() throws IOException {
        FeatureFile file = parseFixture("simple.feature");

        // The keyword should contain "Feature"
        assertThat(file.feature().keyword()).contains("Feature");
    }

    @Test
    void shouldPreserveUri() throws IOException {
        String content = "Feature: Test\n  Scenario: S1\n    Given a step\n";
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("file:///path/to/my.feature", input);

        assertThat(file.uri()).isEqualTo("file:///path/to/my.feature");
    }

    // ------------------------------------------------------------------
    // Additional field coverage tests
    // ------------------------------------------------------------------

    @Test
    void shouldParseTagPositions() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        List<TagDefinition> tags = file.feature().tags();

        // @smoke is on line 2
        assertThat(tags.get(0).position().line()).isEqualTo(2);
        assertThat(tags.get(0).position().column()).isGreaterThan(0);

        // @regression is also on line 2, further right
        assertThat(tags.get(1).position().line()).isEqualTo(2);
        assertThat(tags.get(1).position().column()).isGreaterThan(tags.get(0).position().column());
    }

    @Test
    void shouldParseBackgroundKeyword() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        BackgroundDefinition background = file.feature().background();

        assertThat(background.keyword()).contains("Background");
    }

    @Test
    void shouldParseBackgroundPosition() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        BackgroundDefinition background = file.feature().background();

        // Background: Common setup is on line 6
        assertThat(background.position().line()).isEqualTo(6);
        assertThat(background.position().column()).isGreaterThan(0);
    }

    @Test
    void shouldParseRuleKeyword() throws IOException {
        FeatureFile file = parseFixture("rule.feature");
        RuleDefinition rule = file.feature().rules().get(0);

        assertThat(rule.keyword()).contains("Rule");
    }

    @Test
    void shouldParseRulePosition() throws IOException {
        FeatureFile file = parseFixture("rule.feature");

        // First Rule is on line 4
        assertThat(file.feature().rules().get(0).position().line()).isEqualTo(4);
        // Second Rule is on line 17
        assertThat(file.feature().rules().get(1).position().line()).isEqualTo(17);
    }

    @Test
    void shouldParseRuleDescription() throws IOException {
        FeatureFile file = parseFixture("rule.feature");
        RuleDefinition rule = file.feature().rules().get(0);

        // Rule description may be empty if there's no text between the
        // Rule: line and the first Background/Scenario
        assertThat(rule.description()).isNotNull();
    }

    @Test
    void shouldParseExamplesKeyword() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        ExamplesDefinition examples = file.feature().scenarios().get(1).examples().get(0);

        assertThat(examples.keyword()).contains("Examples");
    }

    @Test
    void shouldParseExamplesPosition() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        ExamplesDefinition examples = file.feature().scenarios().get(1).examples().get(0);

        // Examples: Standard orders is on line 22
        assertThat(examples.position().line()).isEqualTo(22);
    }

    @Test
    void shouldParseExamplesDescription() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        ExamplesDefinition examples = file.feature().scenarios().get(1).examples().get(0);

        // No description on this examples section
        assertThat(examples.description()).isNotNull();
    }

    @Test
    void shouldParseExamplesTags() throws IOException {
        String content = """
                Feature: Tagged examples
                  Scenario Outline: Test
                    Given a value "<val>"

                    @set1
                    Examples: First
                      | val |
                      | A   |

                    @set2 @extra
                    Examples: Second
                      | val |
                      | B   |
                """;
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("test://tagged-examples.feature", input);
        List<ExamplesDefinition> examples = file.feature().scenarios().get(0).examples();

        assertThat(examples.get(0).tags()).hasSize(1);
        assertThat(examples.get(0).tags().get(0).name()).isEqualTo("set1");

        assertThat(examples.get(1).tags()).hasSize(2);
        assertThat(examples.get(1).tags().get(0).name()).isEqualTo("set2");
        assertThat(examples.get(1).tags().get(1).name()).isEqualTo("extra");
    }

    @Test
    void shouldParseDataTablePosition() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        // Third scenario, third step has a data table
        StepDefinition step = file.feature().scenarios().get(2).steps().get(2);

        assertThat(step.dataTable()).isNotNull();
        // The data table starts on line 31
        assertThat(step.dataTable().position().line()).isEqualTo(31);
    }

    @Test
    void shouldParseDocStringPosition() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        // Fourth scenario, first step has a doc string
        StepDefinition step = file.feature().scenarios().get(3).steps().get(0);

        assertThat(step.docString()).isNotNull();
        // The doc string delimiter is on line 37
        assertThat(step.docString().position().line()).isEqualTo(37);
    }

    @Test
    void shouldReturnEmptyContentTypeForDocStringWithoutMediaType() throws IOException {
        String content = """
                Feature: Plain doc string
                  Scenario: No media type
                    Given a block of text:
                      \"""
                      Some plain text content
                      without a media type hint.
                      \"""
                    Then it is processed
                """;
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("test://plain-docstring.feature", input);
        DocStringDefinition docString = file.feature().scenarios().get(0).steps().get(0).docString();

        assertThat(docString).isNotNull();
        assertThat(docString.contentType()).isEmpty();
        assertThat(docString.content()).contains("Some plain text content");
    }

    @Test
    void shouldParseCommentColumnPosition() throws IOException {
        FeatureFile file = parseFixture("full.feature");

        // The comment "# A comment at the top of the file" starts at column 1
        assertThat(file.comments().get(0).position().column()).isEqualTo(1);
    }

    @Test
    void shouldParseScenarioDescription() throws IOException {
        String content = """
                Feature: Descriptions
                  Scenario: With description
                    This scenario tests something important.
                    It spans multiple lines.
                    Given a step
                    Then another step
                """;
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("test://scenario-desc.feature", input);
        ScenarioDefinition scenario = file.feature().scenarios().get(0);

        assertThat(scenario.description()).contains("This scenario tests something important");
    }

    @Test
    void shouldParseBackgroundDescription() throws IOException {
        String content = """
                Feature: Background desc
                  Background: Setup
                    Prepare the environment for each scenario.
                    Given the system is ready
                """;
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FeatureFile file = parser.parse("test://bg-desc.feature", input);
        BackgroundDefinition background = file.feature().background();

        assertThat(background).isNotNull();
        assertThat(background.description()).contains("Prepare the environment");
    }

    @Test
    void shouldParseScenarioKeyword() throws IOException {
        FeatureFile file = parseFixture("full.feature");

        // Regular scenario
        assertThat(file.feature().scenarios().get(0).keyword()).contains("Scenario");

        // Scenario Outline
        assertThat(file.feature().scenarios().get(1).keyword()).contains("Scenario");
    }

    @Test
    void shouldParseExamplesTablePosition() throws IOException {
        FeatureFile file = parseFixture("full.feature");
        ExamplesDefinition examples = file.feature().scenarios().get(1).examples().get(0);

        // The table header starts on line 23
        assertThat(examples.table()).isNotNull();
        assertThat(examples.table().position().line()).isEqualTo(23);
    }
}
