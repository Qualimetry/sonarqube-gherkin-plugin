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

import com.qualimetry.sonar.gherkin.analyzer.parser.FeatureParser;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureMetricsTest {

    private final FeatureParser parser = new FeatureParser();

    @Test
    void shouldComputeMetricsForSimpleFeature() throws IOException {
        String content = """
                Feature: User authentication
                  Users must be able to log in.

                  Scenario: Successful login
                    Given the user is on the login page
                    When they enter valid credentials
                    Then they are redirected to the dashboard
                """;

        FeatureFile file = parse(content);
        FeatureMetrics.MetricResult result = FeatureMetrics.compute(file, content);

        assertThat(result.classes()).isEqualTo(1);
        assertThat(result.functions()).isEqualTo(1);
        assertThat(result.statements()).isEqualTo(3);
        assertThat(result.ncloc()).isEqualTo(6);
        assertThat(result.commentLines()).isEqualTo(0);
    }

    @Test
    void shouldCountCommentsAndBlankLines() throws IOException {
        String content = """
                # This is a comment
                Feature: Shopping cart
                
                  # Another comment
                  Scenario: Add item to cart
                    Given the user is browsing products
                    When they add a product to the cart
                    Then the cart count increases by one
                """;

        FeatureFile file = parse(content);
        FeatureMetrics.MetricResult result = FeatureMetrics.compute(file, content);

        assertThat(result.commentLines()).isEqualTo(2);
        // Code lines: Feature, Scenario, Given, When, Then = 5
        assertThat(result.ncloc()).isEqualTo(5);
        assertThat(result.classes()).isEqualTo(1);
        assertThat(result.functions()).isEqualTo(1);
        assertThat(result.statements()).isEqualTo(3);
    }

    @Test
    void shouldCountBackgroundSteps() throws IOException {
        String content = """
                Feature: Order management

                  Background:
                    Given the user is logged in

                  Scenario: View orders
                    When the user navigates to the orders page
                    Then they see a list of orders

                  Scenario: Cancel order
                    When the user selects an order
                    And they click cancel
                    Then the order status changes to cancelled
                """;

        FeatureFile file = parse(content);
        FeatureMetrics.MetricResult result = FeatureMetrics.compute(file, content);

        assertThat(result.classes()).isEqualTo(1);
        assertThat(result.functions()).isEqualTo(2);
        // 1 background step + 2 steps + 3 steps = 6
        assertThat(result.statements()).isEqualTo(6);
    }

    @Test
    void shouldCountScenariosInsideRules() throws IOException {
        String content = """
                Feature: Payment processing

                  Rule: Credit card payments

                    Scenario: Valid credit card
                      Given the user has a valid credit card
                      When they submit the payment
                      Then the payment is accepted

                  Rule: Bank transfer payments

                    Background:
                      Given the user has a bank account

                    Scenario: Successful transfer
                      When they initiate a transfer
                      Then the transfer is processed
                """;

        FeatureFile file = parse(content);
        FeatureMetrics.MetricResult result = FeatureMetrics.compute(file, content);

        assertThat(result.classes()).isEqualTo(1);
        assertThat(result.functions()).isEqualTo(2);
        // 3 steps in first scenario + 1 background step + 2 steps in second scenario = 6
        assertThat(result.statements()).isEqualTo(6);
    }

    @Test
    void shouldHandleEmptyFile() throws IOException {
        String content = "";
        FeatureFile file = parse(content);
        FeatureMetrics.MetricResult result = FeatureMetrics.compute(file, content);

        assertThat(result.classes()).isEqualTo(0);
        assertThat(result.functions()).isEqualTo(0);
        assertThat(result.statements()).isEqualTo(0);
        assertThat(result.ncloc()).isEqualTo(0);
        assertThat(result.commentLines()).isEqualTo(0);
    }

    @Test
    void shouldHandleNullRawContent() throws IOException {
        String content = "Feature: Test\n  Scenario: Test\n    Given something\n";
        FeatureFile file = parse(content);
        FeatureMetrics.MetricResult result = FeatureMetrics.compute(file, null);

        // Raw content is null - line metrics are 0 but tree metrics still work
        assertThat(result.ncloc()).isEqualTo(0);
        assertThat(result.commentLines()).isEqualTo(0);
        assertThat(result.classes()).isEqualTo(1);
        assertThat(result.functions()).isEqualTo(1);
        assertThat(result.statements()).isEqualTo(1);
    }

    @Test
    void shouldHandleCommentOnlyFile() throws IOException {
        String content = "# Just a comment\n# Another comment\n";
        FeatureFile file = parse(content);
        FeatureMetrics.MetricResult result = FeatureMetrics.compute(file, content);

        assertThat(result.classes()).isEqualTo(0);
        assertThat(result.functions()).isEqualTo(0);
        assertThat(result.statements()).isEqualTo(0);
        assertThat(result.commentLines()).isEqualTo(2);
        assertThat(result.ncloc()).isEqualTo(0);
    }

    private FeatureFile parse(String content) throws IOException {
        return parser.parse("test.feature",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }
}
