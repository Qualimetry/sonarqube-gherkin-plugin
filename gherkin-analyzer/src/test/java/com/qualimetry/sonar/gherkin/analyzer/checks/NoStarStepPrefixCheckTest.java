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

class NoStarStepPrefixCheckTest {

    @Test
    void shouldNotRaiseIssueOnExplicitKeywords() {
        CheckVerifier.verifyNoIssues(
                new NoStarStepPrefixCheck(),
                "checks/no-star-step-prefix/compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnStarPrefix() {
        CheckVerifier.verify(
                new NoStarStepPrefixCheck(),
                "checks/no-star-step-prefix/noncompliant.feature");
    }

    @Test
    void shouldRaiseIssueOnFirstStarStepInScenario() {
        // Verifies the simplified keyword-only condition
        // correctly detects * as the first step in a scenario (where keywordType is
        // UNKNOWN). The UNKNOWN condition was removed to eliminate the overlap with
        // NoUnknownStepTypeCheck — this test proves * is still caught by keyword alone.
        CheckVerifier.verify(
                new NoStarStepPrefixCheck(),
                "checks/no-star-step-prefix/first-star-step-noncompliant.feature");
    }
}
