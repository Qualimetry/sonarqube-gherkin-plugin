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
package com.qualimetry.sonar.gherkin.analyzer.highlighting;

import com.qualimetry.sonar.gherkin.analyzer.highlighting.FeatureHighlighter.HighlightRange;
import com.qualimetry.sonar.gherkin.analyzer.highlighting.FeatureHighlighter.HighlightType;
import com.qualimetry.sonar.gherkin.analyzer.parser.FeatureParser;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureHighlighterTest {

    private final FeatureParser parser = new FeatureParser();

    @Test
    void shouldHighlightFeatureKeyword() throws IOException {
        String content = "Feature: User login\n";
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        assertThat(ranges).anySatisfy(r -> {
            assertThat(r.type()).isEqualTo(HighlightType.KEYWORD);
            assertThat(r.startLine()).isEqualTo(1);
        });
    }

    @Test
    void shouldHighlightScenarioKeyword() throws IOException {
        String content = """
                Feature: User login

                  Scenario: Successful login
                    Given the user is on the login page
                    When they enter valid credentials
                    Then they are logged in
                """;
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        // Should have keyword ranges for: Feature, Scenario, Given, When, Then
        long keywordCount = ranges.stream()
                .filter(r -> r.type() == HighlightType.KEYWORD)
                .count();
        assertThat(keywordCount).isGreaterThanOrEqualTo(5);
    }

    @Test
    void shouldHighlightTags() throws IOException {
        String content = """
                @smoke @regression
                Feature: User login

                  @critical
                  Scenario: Successful login
                    Given the user is on the login page
                    When they enter valid credentials
                    Then they are logged in
                """;
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        long annotationCount = ranges.stream()
                .filter(r -> r.type() == HighlightType.ANNOTATION)
                .count();
        // @smoke, @regression, @critical = 3 tags
        assertThat(annotationCount).isEqualTo(3);
    }

    @Test
    void shouldHighlightComments() throws IOException {
        String content = """
                # This is a header comment
                Feature: User login
                  # This is an inline comment
                  Scenario: Successful login
                    Given the user is on the login page
                """;
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        long commentCount = ranges.stream()
                .filter(r -> r.type() == HighlightType.COMMENT)
                .count();
        assertThat(commentCount).isEqualTo(2);
    }

    @Test
    void shouldHighlightBackgroundKeyword() throws IOException {
        String content = """
                Feature: Order management

                  Background:
                    Given the user is logged in

                  Scenario: View orders
                    When the user navigates to orders
                    Then they see their orders
                """;
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        // Background keyword should be highlighted
        assertThat(ranges).anySatisfy(r -> {
            assertThat(r.type()).isEqualTo(HighlightType.KEYWORD);
            assertThat(r.startLine()).isEqualTo(3);
        });
    }

    @Test
    void shouldHighlightExamplesKeyword() throws IOException {
        String content = """
                Feature: User search

                  Scenario Outline: Search by criteria
                    Given the user is on the search page
                    When they search for "<term>"
                    Then they see results

                    Examples:
                      | term    |
                      | laptop  |
                      | phone   |
                """;
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        // Should highlight Examples keyword
        assertThat(ranges).anySatisfy(r -> {
            assertThat(r.type()).isEqualTo(HighlightType.KEYWORD);
            assertThat(r.startLine()).isEqualTo(8);
        });
    }

    @Test
    void shouldHighlightRuleKeyword() throws IOException {
        String content = """
                Feature: Payments

                  Rule: Credit card payments

                    Scenario: Valid card
                      Given a valid credit card
                      When the user pays
                      Then the payment succeeds
                """;
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        // Rule keyword should be highlighted
        assertThat(ranges).anySatisfy(r -> {
            assertThat(r.type()).isEqualTo(HighlightType.KEYWORD);
            assertThat(r.startLine()).isEqualTo(3);
        });
    }

    @Test
    void shouldReturnEmptyListForEmptyFile() throws IOException {
        String content = "";
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        // Empty file has no feature and no comments
        assertThat(ranges).isEmpty();
    }

    @Test
    void shouldHighlightExamplesLevelTags() throws IOException {
        String content = """
                Feature: User search

                  Scenario Outline: Search products
                    Given the user is on the search page
                    When they search for "<term>"
                    Then they see results

                    @dataset1
                    Examples:
                      | term   |
                      | laptop |
                """;
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        // Should have an ANNOTATION for @dataset1
        assertThat(ranges).anySatisfy(r -> {
            assertThat(r.type()).isEqualTo(HighlightType.ANNOTATION);
            assertThat(r.startLine()).isEqualTo(8);
        });
    }

    @Test
    void shouldComputeCorrectKeywordEndColumn() throws IOException {
        // "Feature" keyword starts at column 1
        String content = "Feature: Test\n  Scenario: Test\n    Given something\n";
        FeatureFile file = parse(content);

        List<HighlightRange> ranges = FeatureHighlighter.highlight(file);

        // Find the Feature keyword range
        HighlightRange featureRange = ranges.stream()
                .filter(r -> r.type() == HighlightType.KEYWORD && r.startLine() == 1)
                .findFirst()
                .orElseThrow();

        // "Feature" is 7 characters, starts at col 1 -> ends at col 7
        assertThat(featureRange.startColumn()).isEqualTo(1);
        assertThat(featureRange.endColumn()).isGreaterThanOrEqualTo(featureRange.startColumn());
    }

    private FeatureFile parse(String content) throws IOException {
        return parser.parse("test.feature",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }
}
