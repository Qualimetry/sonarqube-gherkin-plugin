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

class NoRedundantRuleTagsCheckTest {

    @Test
    void shouldNotRaiseIssueOnCompliantFile() {
        CheckVerifier.verifyNoIssues(
                new NoRedundantRuleTagsCheck(),
                "checks/no-redundant-rule-tags/compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnRedundantRuleTags() {
        CheckVerifier.verify(
                new NoRedundantRuleTagsCheck(),
                "checks/no-redundant-rule-tags/noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueWhenNoRulesExist() {
        CheckVerifier.verifyNoIssues(
                new NoRedundantRuleTagsCheck(),
                "checks/no-redundant-rule-tags/no-rules.feature");
    }

    @Test
    void shouldNotFlagTagsThatOverlapWithFeatureLevel() {
        // When a tag exists at both Feature and Rule level, the scenario tag
        // duplicating it is Feature-level redundancy (handled by no-redundant-tags),
        // not Rule-level redundancy. This check should not flag it.
        CheckVerifier.verifyNoIssues(
                new NoRedundantRuleTagsCheck(),
                "checks/no-redundant-rule-tags/feature-tag-overlap.feature");
    }
}
