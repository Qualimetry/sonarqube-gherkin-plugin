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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.BackgroundDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.Comment;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FeatureWalker}, verifying that all visitor callbacks
 * are invoked in the correct order for various tree structures.
 */
class FeatureWalkerTest {

    private FeatureParser parser;

    @BeforeEach
    void setUp() {
        parser = new FeatureParser();
    }

    private FeatureFile parse(String content) throws IOException {
        return parser.parse("test://walker-test.feature",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    // ------------------------------------------------------------------
    // Simple feature: visit/leave order
    // ------------------------------------------------------------------

    @Test
    void shouldCallVisitAndLeaveForSimpleFeature() throws IOException {
        String content = """
                Feature: Simple
                  Scenario: First
                    Given a step
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitFeatureFile(FeatureFile f) {
                events.add("visitFeatureFile");
            }

            @Override
            public void visitFeature(FeatureDefinition feature) {
                events.add("visitFeature:" + feature.name());
            }

            @Override
            public void visitScenario(ScenarioDefinition scenario) {
                events.add("visitScenario:" + scenario.name());
            }

            @Override
            public void visitStep(StepDefinition step) {
                events.add("visitStep:" + step.text());
            }

            @Override
            public void leaveScenario(ScenarioDefinition scenario) {
                events.add("leaveScenario:" + scenario.name());
            }

            @Override
            public void leaveFeature(FeatureDefinition feature) {
                events.add("leaveFeature:" + feature.name());
            }

            @Override
            public void leaveFeatureFile(FeatureFile f) {
                events.add("leaveFeatureFile");
            }
        });

        assertThat(events).containsExactly(
                "visitFeatureFile",
                "visitFeature:Simple",
                "visitScenario:First",
                "visitStep:a step",
                "leaveScenario:First",
                "leaveFeature:Simple",
                "leaveFeatureFile");
    }

    // ------------------------------------------------------------------
    // Feature with tags, background, comments
    // ------------------------------------------------------------------

    @Test
    void shouldWalkTagsBackgroundAndComments() throws IOException {
        String content = """
                # A comment
                @smoke @regression
                Feature: Full
                  Background: Setup
                    Given the system is ready
                  Scenario: Test
                    When something happens
                    Then a result occurs
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitFeatureFile(FeatureFile f) {
                events.add("visitFeatureFile");
            }

            @Override
            public void visitFeature(FeatureDefinition feature) {
                events.add("visitFeature");
            }

            @Override
            public void visitTag(TagDefinition tag) {
                events.add("visitTag:" + tag.name());
            }

            @Override
            public void visitBackground(BackgroundDefinition bg) {
                events.add("visitBackground:" + bg.name());
            }

            @Override
            public void visitScenario(ScenarioDefinition scenario) {
                events.add("visitScenario:" + scenario.name());
            }

            @Override
            public void visitStep(StepDefinition step) {
                events.add("visitStep:" + step.text());
            }

            @Override
            public void visitComment(Comment comment) {
                events.add("visitComment");
            }

            @Override
            public void leaveFeature(FeatureDefinition feature) {
                events.add("leaveFeature");
            }

            @Override
            public void leaveScenario(ScenarioDefinition scenario) {
                events.add("leaveScenario");
            }

            @Override
            public void leaveFeatureFile(FeatureFile f) {
                events.add("leaveFeatureFile");
            }
        });

        assertThat(events).containsExactly(
                "visitFeatureFile",
                "visitFeature",
                "visitTag:smoke",
                "visitTag:regression",
                "visitBackground:Setup",
                "visitStep:the system is ready",
                "visitScenario:Test",
                "visitStep:something happens",
                "visitStep:a result occurs",
                "leaveScenario",
                "leaveFeature",
                "visitComment",
                "leaveFeatureFile");
    }

    // ------------------------------------------------------------------
    // Scenario with examples
    // ------------------------------------------------------------------

    @Test
    void shouldWalkExamples() throws IOException {
        String content = """
                Feature: Outline
                  Scenario Outline: Parameterized
                    Given a value of "<val>"
                    Then the result is "<res>"

                    Examples: Set one
                      | val | res |
                      | A   | X   |
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitScenario(ScenarioDefinition scenario) {
                events.add("visitScenario");
            }

            @Override
            public void visitStep(StepDefinition step) {
                events.add("visitStep");
            }

            @Override
            public void visitExamples(ExamplesDefinition examples) {
                events.add("visitExamples:" + examples.name());
            }

            @Override
            public void leaveScenario(ScenarioDefinition scenario) {
                events.add("leaveScenario");
            }
        });

        assertThat(events).containsExactly(
                "visitScenario",
                "visitStep",
                "visitStep",
                "visitExamples:Set one",
                "leaveScenario");
    }

    // ------------------------------------------------------------------
    // Feature with Rule blocks (CRITICAL: recursive rule walking)
    // ------------------------------------------------------------------

    @Test
    void shouldWalkRulesAndTheirChildren() throws IOException {
        String content = """
                Feature: Rules test
                  Rule: First rule
                    Background: Rule background
                      Given a precondition

                    Scenario: Inside rule
                      When an action
                      Then a result

                  Rule: Second rule
                    @rule-tag
                    Scenario: Another inside rule
                      Given another precondition
                      When another action
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitFeatureFile(FeatureFile f) {
                events.add("visitFeatureFile");
            }

            @Override
            public void visitFeature(FeatureDefinition feature) {
                events.add("visitFeature");
            }

            @Override
            public void visitRule(RuleDefinition rule) {
                events.add("visitRule:" + rule.name());
            }

            @Override
            public void visitTag(TagDefinition tag) {
                events.add("visitTag:" + tag.name());
            }

            @Override
            public void visitBackground(BackgroundDefinition bg) {
                events.add("visitBackground:" + bg.name());
            }

            @Override
            public void visitScenario(ScenarioDefinition scenario) {
                events.add("visitScenario:" + scenario.name());
            }

            @Override
            public void visitStep(StepDefinition step) {
                events.add("visitStep:" + step.text());
            }

            @Override
            public void leaveRule(RuleDefinition rule) {
                events.add("leaveRule:" + rule.name());
            }

            @Override
            public void leaveScenario(ScenarioDefinition scenario) {
                events.add("leaveScenario:" + scenario.name());
            }

            @Override
            public void leaveFeature(FeatureDefinition feature) {
                events.add("leaveFeature");
            }

            @Override
            public void leaveFeatureFile(FeatureFile f) {
                events.add("leaveFeatureFile");
            }
        });

        // Note: @rule-tag is placed on the scenario, not on the rule itself.
        // The walker visits: visitScenario -> scenario tags -> steps -> leaveScenario
        assertThat(events).containsExactly(
                "visitFeatureFile",
                "visitFeature",
                "visitRule:First rule",
                "visitBackground:Rule background",
                "visitStep:a precondition",
                "visitScenario:Inside rule",
                "visitStep:an action",
                "visitStep:a result",
                "leaveScenario:Inside rule",
                "leaveRule:First rule",
                "visitRule:Second rule",
                "visitScenario:Another inside rule",
                "visitTag:rule-tag",
                "visitStep:another precondition",
                "visitStep:another action",
                "leaveScenario:Another inside rule",
                "leaveRule:Second rule",
                "leaveFeature",
                "leaveFeatureFile");
    }

    // ------------------------------------------------------------------
    // Scenario tags inside rules
    // ------------------------------------------------------------------

    @Test
    void shouldWalkScenarioTagsInsideRules() throws IOException {
        String content = """
                Feature: Tag test
                  Rule: Business rule
                    @wip @critical
                    Scenario: Tagged scenario
                      Given a step
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitTag(TagDefinition tag) {
                events.add("tag:" + tag.name());
            }

            @Override
            public void visitScenario(ScenarioDefinition scenario) {
                events.add("scenario:" + scenario.name());
            }
        });

        // The walker calls visitScenario first, then walks the scenario's tags
        assertThat(events).containsExactly(
                "scenario:Tagged scenario",
                "tag:wip",
                "tag:critical");
    }

    // ------------------------------------------------------------------
    // Empty / null feature handling
    // ------------------------------------------------------------------

    @Test
    void shouldHandleEmptyFile() throws IOException {
        String content = "";
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitFeatureFile(FeatureFile f) {
                events.add("visitFeatureFile");
            }

            @Override
            public void visitFeature(FeatureDefinition feature) {
                events.add("visitFeature");
            }

            @Override
            public void leaveFeatureFile(FeatureFile f) {
                events.add("leaveFeatureFile");
            }
        });

        // visitFeature should NOT be called for an empty file
        assertThat(events).containsExactly(
                "visitFeatureFile",
                "leaveFeatureFile");
    }

    @Test
    void shouldHandleCommentOnlyFile() throws IOException {
        String content = "# Just a comment\n# Another comment\n";
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitFeatureFile(FeatureFile f) {
                events.add("visitFeatureFile");
            }

            @Override
            public void visitComment(Comment comment) {
                events.add("visitComment");
            }

            @Override
            public void leaveFeatureFile(FeatureFile f) {
                events.add("leaveFeatureFile");
            }
        });

        assertThat(events).containsExactly(
                "visitFeatureFile",
                "visitComment",
                "visitComment",
                "leaveFeatureFile");
    }

    // ------------------------------------------------------------------
    // Feature with multiple scenarios (no Rule, no Background)
    // ------------------------------------------------------------------

    @Test
    void shouldWalkMultipleScenariosInOrder() throws IOException {
        String content = """
                Feature: Multi
                  Scenario: First
                    Given step one
                  Scenario: Second
                    Given step two
                  Scenario: Third
                    Given step three
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitScenario(ScenarioDefinition scenario) {
                events.add("scenario:" + scenario.name());
            }
        });

        assertThat(events).containsExactly(
                "scenario:First",
                "scenario:Second",
                "scenario:Third");
    }

    // ------------------------------------------------------------------
    // Mixed: top-level scenarios AND rules
    // ------------------------------------------------------------------

    @Test
    void shouldWalkTopLevelScenariosBeforeRules() throws IOException {
        String content = """
                Feature: Mixed
                  Scenario: Top level
                    Given a step

                  Rule: A rule
                    Scenario: Inside rule
                      Given another step
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitScenario(ScenarioDefinition scenario) {
                events.add("scenario:" + scenario.name());
            }

            @Override
            public void visitRule(RuleDefinition rule) {
                events.add("rule:" + rule.name());
            }

            @Override
            public void leaveRule(RuleDefinition rule) {
                events.add("leaveRule:" + rule.name());
            }
        });

        assertThat(events).containsExactly(
                "scenario:Top level",
                "rule:A rule",
                "scenario:Inside rule",
                "leaveRule:A rule");
    }

    // ------------------------------------------------------------------
    // Scenario Outline with multiple Examples inside a Rule
    // ------------------------------------------------------------------

    @Test
    void shouldWalkExamplesInsideRule() throws IOException {
        String content = """
                Feature: Rule with outline
                  Rule: Pricing
                    Scenario Outline: Calculate price
                      Given a product priced at "<price>"
                      Then the total is "<total>"

                      Examples: Standard
                        | price | total |
                        | 10    | 10    |

                      Examples: Discounted
                        | price | total |
                        | 10    | 8     |
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitRule(RuleDefinition rule) {
                events.add("rule:" + rule.name());
            }

            @Override
            public void visitScenario(ScenarioDefinition scenario) {
                events.add("scenario:" + scenario.name());
            }

            @Override
            public void visitExamples(ExamplesDefinition examples) {
                events.add("examples:" + examples.name());
            }

            @Override
            public void leaveScenario(ScenarioDefinition scenario) {
                events.add("leaveScenario");
            }

            @Override
            public void leaveRule(RuleDefinition rule) {
                events.add("leaveRule");
            }
        });

        assertThat(events).containsExactly(
                "rule:Pricing",
                "scenario:Calculate price",
                "examples:Standard",
                "examples:Discounted",
                "leaveScenario",
                "leaveRule");
    }

    // ------------------------------------------------------------------
    // Rule-level tags (tags on the Rule: keyword itself)
    // ------------------------------------------------------------------

    @Test
    void shouldWalkRuleLevelTags() throws IOException {
        String content = """
                Feature: Rule tags
                  @business @critical
                  Rule: Important rule
                    Scenario: Test
                      Given a step
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitRule(RuleDefinition rule) {
                events.add("rule:" + rule.name());
            }

            @Override
            public void visitTag(TagDefinition tag) {
                events.add("tag:" + tag.name());
            }

            @Override
            public void leaveRule(RuleDefinition rule) {
                events.add("leaveRule");
            }
        });

        // Rule-level tags should be visited after visitRule, before scenarios
        assertThat(events).containsExactly(
                "rule:Important rule",
                "tag:business",
                "tag:critical",
                "leaveRule");
    }

    // ------------------------------------------------------------------
    // Examples-level tags
    // ------------------------------------------------------------------

    @Test
    void shouldWalkExamplesLevelTags() throws IOException {
        String content = """
                Feature: Examples tags
                  Scenario Outline: Parameterized
                    Given a value "<val>"

                    @set1 @primary
                    Examples: First set
                      | val |
                      | A   |

                    @set2
                    Examples: Second set
                      | val |
                      | B   |
                """;
        FeatureFile file = parse(content);
        List<String> events = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitExamples(ExamplesDefinition examples) {
                events.add("examples:" + examples.name());
            }

            @Override
            public void visitTag(TagDefinition tag) {
                events.add("tag:" + tag.name());
            }
        });

        // Examples tags should be visited after visitExamples
        assertThat(events).containsExactly(
                "examples:First set",
                "tag:set1",
                "tag:primary",
                "examples:Second set",
                "tag:set2");
    }

    // ------------------------------------------------------------------
    // All tag levels in a single walk
    // ------------------------------------------------------------------

    @Test
    void shouldVisitTagsAtAllLevels() throws IOException {
        String content = """
                @feature-tag
                Feature: All tags
                  @scenario-tag
                  Scenario Outline: Tagged
                    Given a value "<val>"

                    @examples-tag
                    Examples: Tagged examples
                      | val |
                      | A   |
                """;
        FeatureFile file = parse(content);
        List<String> tagNames = new ArrayList<>();

        FeatureWalker.walk(file, new FeatureVisitor() {
            @Override
            public void visitTag(TagDefinition tag) {
                tagNames.add(tag.name());
            }
        });

        assertThat(tagNames).containsExactly(
                "feature-tag",
                "scenario-tag",
                "examples-tag");
    }

    // ------------------------------------------------------------------
    // No-op default visitor (smoke test)
    // ------------------------------------------------------------------

    @Test
    void shouldNotFailWithDefaultNoOpVisitor() throws IOException {
        String content = """
                @tag
                Feature: No-op test
                  Background: Setup
                    Given a step
                  Scenario: Test
                    When action
                    Then result
                  Rule: R
                    Scenario: Inner
                      Given step
                """;
        FeatureFile file = parse(content);

        // Walk with default no-op visitor - should not throw
        FeatureWalker.walk(file, new FeatureVisitor() {});
    }

    // ------------------------------------------------------------------
    // Verify fixture file walk (using full.feature from parser tests)
    // ------------------------------------------------------------------

    @Test
    void shouldWalkParsedFixtureFile() throws IOException {
        try (var input = getClass().getResourceAsStream("/parser/full.feature")) {
            assertThat(input).isNotNull();
            FeatureFile file = parser.parse("test://full.feature", input);

            List<String> events = new ArrayList<>();
            FeatureWalker.walk(file, new FeatureVisitor() {
                @Override
                public void visitFeatureFile(FeatureFile f) {
                    events.add("visitFeatureFile");
                }

                @Override
                public void visitFeature(FeatureDefinition feature) {
                    events.add("visitFeature");
                }

                @Override
                public void visitTag(TagDefinition tag) {
                    events.add("tag:" + tag.name());
                }

                @Override
                public void visitBackground(BackgroundDefinition bg) {
                    events.add("background");
                }

                @Override
                public void visitScenario(ScenarioDefinition scenario) {
                    events.add("scenario:" + scenario.name());
                }

                @Override
                public void visitStep(StepDefinition step) {
                    events.add("step");
                }

                @Override
                public void visitExamples(ExamplesDefinition examples) {
                    events.add("examples");
                }

                @Override
                public void visitComment(Comment c) {
                    events.add("comment");
                }

                @Override
                public void leaveFeature(FeatureDefinition feature) {
                    events.add("leaveFeature");
                }

                @Override
                public void leaveFeatureFile(FeatureFile f) {
                    events.add("leaveFeatureFile");
                }
            });

            // Verify the structure: Feature has 2 tags, background with 2 steps,
            // 4 scenarios with various steps, examples, and comments
            assertThat(events).startsWith("visitFeatureFile", "visitFeature");
            assertThat(events).endsWith("leaveFeature", "comment", "leaveFeatureFile");
            assertThat(events).contains("tag:smoke", "tag:regression", "background", "examples");

            // Count scenarios visited
            long scenarioCount = events.stream().filter(e -> e.startsWith("scenario:")).count();
            assertThat(scenarioCount).isEqualTo(4);
        }
    }

    // ------------------------------------------------------------------
    // Verify rule.feature fixture walk
    // ------------------------------------------------------------------

    @Test
    void shouldWalkRuleFixtureFile() throws IOException {
        try (var input = getClass().getResourceAsStream("/parser/rule.feature")) {
            assertThat(input).isNotNull();
            FeatureFile file = parser.parse("test://rule.feature", input);

            List<String> events = new ArrayList<>();
            FeatureWalker.walk(file, new FeatureVisitor() {
                @Override
                public void visitRule(RuleDefinition rule) {
                    events.add("rule:" + rule.name());
                }

                @Override
                public void visitBackground(BackgroundDefinition bg) {
                    events.add("background:" + bg.name());
                }

                @Override
                public void visitScenario(ScenarioDefinition scenario) {
                    events.add("scenario:" + scenario.name());
                }

                @Override
                public void visitStep(StepDefinition step) {
                    events.add("step:" + step.text());
                }

                @Override
                public void leaveRule(RuleDefinition rule) {
                    events.add("leaveRule:" + rule.name());
                }
            });

            // Two rules
            assertThat(events).contains(
                    "rule:Free shipping for premium members",
                    "rule:Loyalty points accumulation");

            // First rule has a background and 2 scenarios
            assertThat(events).contains("background:Premium account");

            // All steps inside rules should be visited
            long stepCount = events.stream().filter(e -> e.startsWith("step:")).count();
            assertThat(stepCount).isGreaterThanOrEqualTo(5);
        }
    }
}
