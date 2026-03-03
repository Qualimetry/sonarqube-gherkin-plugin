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
package com.qualimetry.sonar.gherkin.analyzer.visitor;

import com.qualimetry.sonar.gherkin.analyzer.parser.FeatureParser;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.check.Rule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BaseCheck}, verifying issue-reporting helpers and
 * context management.
 */
class BaseCheckTest {

    private FeatureParser parser;

    @BeforeEach
    void setUp() {
        parser = new FeatureParser();
    }

    private FeatureFile parse(String content) throws IOException {
        return parser.parse("test://base-check-test.feature",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    // ------------------------------------------------------------------
    // A trivial check that flags every scenario
    // ------------------------------------------------------------------

    @Rule(key = "test-rule")
    static class TestScenarioCheck extends BaseCheck {
        @Override
        public void visitScenario(ScenarioDefinition scenario) {
            addIssue(scenario.position(), "Scenario found: " + scenario.name());
        }
    }

    // ------------------------------------------------------------------
    // A check that uses addLineIssue
    // ------------------------------------------------------------------

    @Rule(key = "test-line-rule")
    static class TestLineCheck extends BaseCheck {
        @Override
        public void visitStep(StepDefinition step) {
            addLineIssue(step.position().line(), "Step at line " + step.position().line());
        }
    }

    // ------------------------------------------------------------------
    // A check that uses addFileIssue
    // ------------------------------------------------------------------

    @Rule(key = "test-file-rule")
    static class TestFileCheck extends BaseCheck {
        @Override
        public void visitFeatureFile(FeatureFile file) {
            if (file.feature() == null) {
                addFileIssue("No feature found in this file.");
            }
        }
    }

    // ------------------------------------------------------------------
    // A check without @Rule annotation
    // ------------------------------------------------------------------

    static class TestUnannotatedCheck extends BaseCheck {
        @Override
        public void visitFeature(FeatureDefinition feature) {
            addFileIssue("Unannotated check triggered.");
        }
    }

    // ------------------------------------------------------------------
    // A check that uses addIssue with cost
    // ------------------------------------------------------------------

    @Rule(key = "test-cost-rule")
    static class TestCostCheck extends BaseCheck {
        @Override
        public void visitScenario(ScenarioDefinition scenario) {
            addIssue(scenario.position(), "Expensive issue", 5.0);
        }
    }

    // ------------------------------------------------------------------
    // A check that uses addLineIssue with cost
    // ------------------------------------------------------------------

    @Rule(key = "test-line-cost-rule")
    static class TestLineCostCheck extends BaseCheck {
        @Override
        public void visitStep(StepDefinition step) {
            addLineIssue(step.position().line(), "Line cost issue", 2.5);
        }
    }

    // ------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------

    @Test
    void shouldReportIssueWithPositionAndRuleKey() throws IOException {
        String content = """
                Feature: Test
                  Scenario: First
                    Given a step
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestScenarioCheck check = new TestScenarioCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        List<Issue> issues = context.getIssues();
        assertThat(issues).hasSize(1);

        Issue issue = issues.get(0);
        assertThat(issue.ruleKey()).isEqualTo("test-rule");
        assertThat(issue.message()).isEqualTo("Scenario found: First");
        assertThat(issue.position()).isNotNull();
        assertThat(issue.line()).isNotNull();
        assertThat(issue.cost()).isNull();
    }

    @Test
    void shouldReportMultipleIssues() throws IOException {
        String content = """
                Feature: Test
                  Scenario: First
                    Given a step
                  Scenario: Second
                    Given another step
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestScenarioCheck check = new TestScenarioCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        assertThat(context.getIssues()).hasSize(2);
        assertThat(context.getIssues().get(0).message()).isEqualTo("Scenario found: First");
        assertThat(context.getIssues().get(1).message()).isEqualTo("Scenario found: Second");
    }

    @Test
    void shouldReportLineIssue() throws IOException {
        String content = """
                Feature: Test
                  Scenario: Steps
                    Given first step
                    When second step
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestLineCheck check = new TestLineCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        List<Issue> issues = context.getIssues();
        assertThat(issues).hasSize(2);

        assertThat(issues.get(0).ruleKey()).isEqualTo("test-line-rule");
        assertThat(issues.get(0).line()).isEqualTo(3);
        assertThat(issues.get(0).position()).isNull(); // addLineIssue does not set position

        assertThat(issues.get(1).line()).isEqualTo(4);
    }

    @Test
    void shouldReportFileIssueForEmptyFile() throws IOException {
        String content = "";
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestFileCheck check = new TestFileCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        List<Issue> issues = context.getIssues();
        assertThat(issues).hasSize(1);

        Issue issue = issues.get(0);
        assertThat(issue.ruleKey()).isEqualTo("test-file-rule");
        assertThat(issue.message()).isEqualTo("No feature found in this file.");
        assertThat(issue.position()).isNull();
        assertThat(issue.line()).isNull();
        assertThat(issue.cost()).isNull();
    }

    @Test
    void shouldNotReportFileIssueWhenFeatureExists() throws IOException {
        String content = """
                Feature: Exists
                  Scenario: A
                    Given a step
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestFileCheck check = new TestFileCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        assertThat(context.getIssues()).isEmpty();
    }

    @Test
    void shouldUseUnknownRuleKeyWhenAnnotationMissing() throws IOException {
        String content = """
                Feature: Unannotated
                  Scenario: S
                    Given a step
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestUnannotatedCheck check = new TestUnannotatedCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).ruleKey()).isEqualTo("unknown");
    }

    @Test
    void shouldReportIssueWithCost() throws IOException {
        String content = """
                Feature: Cost
                  Scenario: Expensive
                    Given a step
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestCostCheck check = new TestCostCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        List<Issue> issues = context.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).cost()).isEqualTo(5.0);
        assertThat(issues.get(0).position()).isNotNull();
    }

    @Test
    void shouldReportLineIssueWithCost() throws IOException {
        String content = """
                Feature: Line cost
                  Scenario: S
                    Given a step
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestLineCostCheck check = new TestLineCostCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        List<Issue> issues = context.getIssues();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).cost()).isEqualTo(2.5);
        assertThat(issues.get(0).position()).isNull();
        assertThat(issues.get(0).line()).isNotNull();
    }

    @Test
    void shouldProvideContextAccessInCheck() throws IOException {
        String content = """
                Feature: Context
                  Scenario: S
                    Given a step
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file, null, "raw content here");

        TestScenarioCheck check = new TestScenarioCheck();
        check.setContext(context);

        assertThat(check.getContext()).isSameAs(context);
        assertThat(check.getContext().getFeatureFile()).isSameAs(file);
        assertThat(check.getContext().getRawContent()).isEqualTo("raw content here");
        assertThat(check.getContext().getInputFile()).isNull();
    }

    @Test
    void shouldReportIssuesForScenariosInsideRules() throws IOException {
        String content = """
                Feature: Rules
                  Rule: Business rule
                    Scenario: Inside rule
                      Given a step
                    Scenario: Also inside rule
                      When an action
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestScenarioCheck check = new TestScenarioCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        // Both scenarios inside the rule should be visited and flagged
        assertThat(context.getIssues()).hasSize(2);
        assertThat(context.getIssues().get(0).message()).isEqualTo("Scenario found: Inside rule");
        assertThat(context.getIssues().get(1).message()).isEqualTo("Scenario found: Also inside rule");
    }

    @Test
    void shouldReportStepsInsideRuleBackground() throws IOException {
        String content = """
                Feature: Rule background
                  Rule: A rule
                    Background: Setup
                      Given precondition one
                      And precondition two
                    Scenario: Test
                      When action
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        TestLineCheck check = new TestLineCheck();
        check.setContext(context);
        FeatureWalker.walk(file, check);

        // 2 background steps + 1 scenario step = 3 issues
        assertThat(context.getIssues()).hasSize(3);
    }

    @Test
    void contextShouldStartWithEmptyIssues() throws IOException {
        String content = "Feature: Empty\n  Scenario: S\n    Given a step\n";
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        assertThat(context.getIssues()).isEmpty();
    }

    @Test
    void shouldAccumulateIssuesFromMultipleChecks() throws IOException {
        String content = """
                Feature: Multi-check
                  Scenario: S
                    Given a step
                """;
        FeatureFile file = parse(content);
        FeatureContext context = new FeatureContext(file);

        // Run two different checks against the same context
        TestScenarioCheck scenarioCheck = new TestScenarioCheck();
        scenarioCheck.setContext(context);
        FeatureWalker.walk(file, scenarioCheck);

        TestLineCheck lineCheck = new TestLineCheck();
        lineCheck.setContext(context);
        FeatureWalker.walk(file, lineCheck);

        // Both checks' issues should accumulate in the same context
        assertThat(context.getIssues()).hasSize(2);
        assertThat(context.getIssues().get(0).ruleKey()).isEqualTo("test-rule");
        assertThat(context.getIssues().get(1).ruleKey()).isEqualTo("test-line-rule");
    }

    @Test
    void shouldNotLeakIssuesWhenCheckIsReusedWithNewContext() throws IOException {
        String contentA = """
                Feature: File A
                  Scenario: SA
                    Given step A
                """;
        String contentB = """
                Feature: File B
                  Scenario: SB
                    Given step B
                  Scenario: SB2
                    Given step B2
                """;

        FeatureFile fileA = parse(contentA);
        FeatureFile fileB = parser.parse("test://file-b.feature",
                new ByteArrayInputStream(contentB.getBytes(StandardCharsets.UTF_8)));

        FeatureContext contextA = new FeatureContext(fileA);
        FeatureContext contextB = new FeatureContext(fileB);

        // Reuse the same check instance across two files (sensor pattern)
        TestScenarioCheck check = new TestScenarioCheck();

        check.setContext(contextA);
        FeatureWalker.walk(fileA, check);
        assertThat(contextA.getIssues()).hasSize(1);
        assertThat(contextA.getIssues().get(0).message()).isEqualTo("Scenario found: SA");

        check.setContext(contextB);
        FeatureWalker.walk(fileB, check);
        assertThat(contextB.getIssues()).hasSize(2);
        assertThat(contextB.getIssues().get(0).message()).isEqualTo("Scenario found: SB");
        assertThat(contextB.getIssues().get(1).message()).isEqualTo("Scenario found: SB2");

        // Original context A should still only have its 1 issue
        assertThat(contextA.getIssues()).hasSize(1);
    }

    @Test
    void contextWithAllConstructorVariants() throws IOException {
        String content = "Feature: Test\n  Scenario: S\n    Given a step\n";
        FeatureFile file = parse(content);

        // Three-arg constructor
        FeatureContext ctx3 = new FeatureContext(file, null, "raw");
        assertThat(ctx3.getRawContent()).isEqualTo("raw");
        assertThat(ctx3.getInputFile()).isNull();

        // Two-arg constructor
        FeatureContext ctx2 = new FeatureContext(file, null);
        assertThat(ctx2.getRawContent()).isNull();

        // One-arg constructor
        FeatureContext ctx1 = new FeatureContext(file);
        assertThat(ctx1.getRawContent()).isNull();
        assertThat(ctx1.getInputFile()).isNull();
    }
}
