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

class NoConflictingTagsCheckTest {

    @Test
    void shouldNotRaiseIssueWithNonConflictingTags() {
        NoConflictingTagsCheck check = new NoConflictingTagsCheck();
        check.setConflictPairs("wip+release-ready");
        CheckVerifier.verifyNoIssues(check,
                "checks/no-conflicting-tags/compliant.feature");
    }

    @Test
    void shouldRaiseIssueForConflictingTags() {
        NoConflictingTagsCheck check = new NoConflictingTagsCheck();
        check.setConflictPairs("wip+release-ready");
        CheckVerifier.verify(check,
                "checks/no-conflicting-tags/noncompliant.feature");
    }

    @Test
    void shouldRaiseIssueWithCustomConfig() {
        NoConflictingTagsCheck check = new NoConflictingTagsCheck();
        check.setConflictPairs("manual+automated");
        CheckVerifier.verify(check,
                "checks/no-conflicting-tags/custom-config.feature");
    }

    @Test
    void shouldNotRaiseIssueWithEmptyConfig() {
        CheckVerifier.verifyNoIssues(
                new NoConflictingTagsCheck(),
                "checks/no-conflicting-tags/empty-config.feature");
    }
}
