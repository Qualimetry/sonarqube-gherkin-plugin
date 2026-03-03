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

class RequiredTagsCheckTest {

    @Test
    void shouldNotRaiseIssueWhenTagsPresent() {
        CheckVerifier.verifyNoIssues(
                new RequiredTagsCheck(),
                "checks/required-tags/compliant.feature");
    }

    @Test
    void shouldRaiseIssueWhenNoTagsPresent() {
        CheckVerifier.verify(
                new RequiredTagsCheck(),
                "checks/required-tags/noncompliant.feature");
    }

    @Test
    void shouldRaiseIssueWithCustomPattern() {
        RequiredTagsCheck check = new RequiredTagsCheck();
        check.setPattern("smoke|regression");
        CheckVerifier.verify(check,
                "checks/required-tags/custom-pattern-noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueOnTaggedScenarioOutline() {
        CheckVerifier.verifyNoIssues(
                new RequiredTagsCheck(),
                "checks/required-tags/outline-compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnUntaggedScenarioOutline() {
        RequiredTagsCheck check = new RequiredTagsCheck();
        check.setPattern("smoke|regression");
        CheckVerifier.verify(check,
                "checks/required-tags/outline-noncompliant.feature");
    }
}
