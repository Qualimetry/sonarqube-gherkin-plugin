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

import com.qualimetry.sonar.gherkin.analyzer.testing.CheckVerifier;
import org.junit.jupiter.api.Test;

class BackgroundNeedsMultipleScenariosCheckTest {

    @Test
    void shouldNotRaiseIssueOnCompliantFile() {
        CheckVerifier.verifyNoIssues(
                new BackgroundNeedsMultipleScenariosCheck(),
                "checks/background-needs-multiple-scenarios/compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnNoncompliantFile() {
        CheckVerifier.verify(
                new BackgroundNeedsMultipleScenariosCheck(),
                "checks/background-needs-multiple-scenarios/noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueWhenNoBackgroundExists() {
        CheckVerifier.verifyNoIssues(
                new BackgroundNeedsMultipleScenariosCheck(),
                "checks/background-needs-multiple-scenarios/no-rules.feature");
    }

    @Test
    void shouldRaiseIssueOnRuleScopedNoncompliantFile() {
        CheckVerifier.verify(
                new BackgroundNeedsMultipleScenariosCheck(),
                "checks/background-needs-multiple-scenarios/rule-scoped-noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueOnRuleScopedCompliantFile() {
        CheckVerifier.verifyNoIssues(
                new BackgroundNeedsMultipleScenariosCheck(),
                "checks/background-needs-multiple-scenarios/rule-scoped-compliant.feature");
    }

    @Test
    void shouldNotRaiseIssueWhenFeatureHasRules() {
        CheckVerifier.verifyNoIssues(
                new BackgroundNeedsMultipleScenariosCheck(),
                "checks/background-needs-multiple-scenarios/feature-with-rules.feature");
    }

    @Test
    void shouldNotRaiseIssueWhenBackgroundHasZeroScenarios() {
        CheckVerifier.verifyNoIssues(
                new BackgroundNeedsMultipleScenariosCheck(),
                "checks/background-needs-multiple-scenarios/zero-scenarios.feature");
    }
}
