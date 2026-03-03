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

class NoDuplicateScenarioBodiesCheckTest {

    @Test
    void shouldNotRaiseIssueForDistinctScenarios() {
        CheckVerifier.verifyNoIssues(
                new NoDuplicateScenarioBodiesCheck(),
                "checks/no-duplicate-scenario-bodies/compliant.feature");
    }

    @Test
    void shouldRaiseIssueForDuplicateScenarios() {
        CheckVerifier.verify(
                new NoDuplicateScenarioBodiesCheck(),
                "checks/no-duplicate-scenario-bodies/noncompliant.feature");
    }

    @Test
    void shouldRaiseIssueForDuplicateScenariosInRule() {
        CheckVerifier.verify(
                new NoDuplicateScenarioBodiesCheck(),
                "checks/no-duplicate-scenario-bodies/rule-scoped-noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueForScenariosWithNoSteps() {
        CheckVerifier.verifyNoIssues(
                new NoDuplicateScenarioBodiesCheck(),
                "checks/no-duplicate-scenario-bodies/no-steps.feature");
    }
}
