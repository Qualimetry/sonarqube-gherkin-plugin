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
package com.qualimetry.sonar.gherkin.analyzer.testing;

import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.junit.jupiter.api.Test;
import org.sonar.check.Rule;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for the {@link CheckVerifier} test harness itself.
 * <p>
 * Uses trivial inner-class checks to exercise the verifier's annotation
 * parsing, issue comparison, and error reporting. Fixture files are stored
 * in {@code src/test/resources/testing/check-verifier/}.
 */
class CheckVerifierTest {

    private static final String FIXTURE_DIR = "testing/check-verifier/";

    // ------------------------------------------------------------------
    // Trivial test checks
    // ------------------------------------------------------------------

    /**
     * A check that flags every scenario with a predictable message.
     */
    @Rule(key = "test-all-scenarios")
    static class AllScenariosCheck extends BaseCheck {
        @Override
        public void visitScenario(ScenarioDefinition scenario) {
            addIssue(scenario.position(), "Test issue on scenario: " + scenario.name());
        }
    }

    /**
     * A no-op check that never raises any issues.
     */
    @Rule(key = "test-noop")
    static class NoOpCheck extends BaseCheck {
        // intentionally empty — no visitor methods overridden
    }

    // ------------------------------------------------------------------
    // verify() tests
    // ------------------------------------------------------------------

    @Test
    void verifyPassesWhenExpectedIssuesMatch() {
        // matching.feature has # Noncompliant before each of its 2 scenarios.
        // AllScenariosCheck flags every scenario → issues at lines 4 and 10.
        CheckVerifier.verify(
                new AllScenariosCheck(),
                FIXTURE_DIR + "matching.feature");
    }

    @Test
    void verifyFailsWhenIssuesAreMissing() {
        // matching.feature expects issues at lines 4 and 10, but NoOpCheck
        // never raises any issues → should fail with "expected ... but found none".
        assertThatThrownBy(() ->
                CheckVerifier.verify(
                        new NoOpCheck(),
                        FIXTURE_DIR + "matching.feature"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("expected")
                .hasMessageContaining("but found none");
    }

    @Test
    void verifyFailsWhenUnexpectedIssuesAreFound() {
        // clean.feature has no # Noncompliant annotations, but AllScenariosCheck
        // flags every scenario → should fail with "unexpected issue".
        assertThatThrownBy(() ->
                CheckVerifier.verify(
                        new AllScenariosCheck(),
                        FIXTURE_DIR + "clean.feature"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("unexpected issue");
    }

    @Test
    void verifyPassesWithCorrectMessage() {
        // with-message.feature has # Noncompliant {{Test issue on scenario: Check stock levels}}
        // AllScenariosCheck produces exactly that message → should pass.
        CheckVerifier.verify(
                new AllScenariosCheck(),
                FIXTURE_DIR + "with-message.feature");
    }

    @Test
    void verifyFailsWhenMessageDoesNotMatch() {
        // wrong-message.feature has # Noncompliant {{This message does not match the actual issue}}
        // AllScenariosCheck produces "Test issue on scenario: Process a credit card payment"
        // → should fail with message mismatch.
        assertThatThrownBy(() ->
                CheckVerifier.verify(
                        new AllScenariosCheck(),
                        FIXTURE_DIR + "wrong-message.feature"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("expected message")
                .hasMessageContaining("but got");
    }

    // ------------------------------------------------------------------
    // verifyNoIssues() tests
    // ------------------------------------------------------------------

    @Test
    void verifyNoIssuesPassesOnCleanFile() {
        // clean.feature with NoOpCheck → zero issues → should pass.
        CheckVerifier.verifyNoIssues(
                new NoOpCheck(),
                FIXTURE_DIR + "clean.feature");
    }

    @Test
    void verifyNoIssuesFailsWhenIssuesAreRaised() {
        // clean.feature with AllScenariosCheck → 2 issues raised → should fail.
        assertThatThrownBy(() ->
                CheckVerifier.verifyNoIssues(
                        new AllScenariosCheck(),
                        FIXTURE_DIR + "clean.feature"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected no issues but found");
    }

    // ------------------------------------------------------------------
    // Annotation extraction tests
    // ------------------------------------------------------------------

    @Test
    void shouldExtractNoncompliantAnnotationsFromRawText() {
        String rawContent = """
                Feature: Test
                  # Noncompliant
                  Scenario: First
                    Given a step
                  # Noncompliant {{Expected message}}
                  Scenario: Second
                    When an action
                """;

        List<TestIssue> issues = CheckVerifier.extractExpectedIssues(rawContent);

        assertThat(issues).hasSize(2);

        // # Noncompliant on line 2 → issue on line 3
        assertThat(issues.get(0).line()).isEqualTo(3);
        assertThat(issues.get(0).message()).isNull();

        // # Noncompliant {{Expected message}} on line 5 → issue on line 6
        assertThat(issues.get(1).line()).isEqualTo(6);
        assertThat(issues.get(1).message()).isEqualTo("Expected message");
    }

    @Test
    void shouldExtractColumnSpecFromAnnotation() {
        String rawContent = """
                Feature: Column test
                  # Noncompliant [[sc=3;ec=15]] {{Column issue}}
                  Scenario: Positioned
                    Given a step
                """;

        List<TestIssue> issues = CheckVerifier.extractExpectedIssues(rawContent);

        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).line()).isEqualTo(3);
        assertThat(issues.get(0).message()).isEqualTo("Column issue");
        assertThat(issues.get(0).startColumn()).isEqualTo(3);
        assertThat(issues.get(0).endColumn()).isEqualTo(15);
    }

    @Test
    void shouldReturnEmptyListWhenNoAnnotations() {
        String rawContent = """
                Feature: No annotations
                  Scenario: Clean
                    Given a step
                """;

        List<TestIssue> issues = CheckVerifier.extractExpectedIssues(rawContent);

        assertThat(issues).isEmpty();
    }

    // ------------------------------------------------------------------
    // Edge case tests
    // ------------------------------------------------------------------

    @Test
    void verifyThrowsForMissingFixtureFile() {
        assertThatThrownBy(() ->
                CheckVerifier.verify(
                        new NoOpCheck(),
                        "nonexistent/path/missing.feature"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Fixture file not found");
    }

    @Test
    void fixturePathForRuleBuildsCorrectPath() {
        String path = CheckTestUtils.fixturePathForRule("feature-file-required", "noncompliant.feature");
        assertThat(path).isEqualTo("checks/feature-file-required/noncompliant.feature");
    }
}
