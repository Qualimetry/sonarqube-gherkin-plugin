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

class ConsistentIndentationCheckTest {

    @Test
    void shouldNotRaiseIssueOnProperlyIndentedFile() {
        CheckVerifier.verifyNoIssues(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnInconsistentIndentation() {
        CheckVerifier.verify(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueWithFourSpaceIndent() {
        ConsistentIndentationCheck check = new ConsistentIndentationCheck();
        check.setIndentation(4);
        CheckVerifier.verifyNoIssues(check,
                "checks/consistent-indentation/four-space-compliant.feature");
    }

    @Test
    void shouldRaiseIssueWithFourSpaceIndentOnWrongLine() {
        ConsistentIndentationCheck check = new ConsistentIndentationCheck();
        check.setIndentation(4);
        CheckVerifier.verify(check,
                "checks/consistent-indentation/four-space-noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueOnRuleBlock() {
        CheckVerifier.verifyNoIssues(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/rule-block.feature");
    }

    @Test
    void shouldRaiseIssueOnRuleBlockNoncompliant() {
        CheckVerifier.verify(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/rule-block-noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueOnNonEnglishCompliant() {
        CheckVerifier.verifyNoIssues(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/non-english-compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnNonEnglishNoncompliant() {
        CheckVerifier.verify(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/non-english-noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueOnCorrectlyIndentedTags() {
        CheckVerifier.verifyNoIssues(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/tags-compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnIncorrectlyIndentedTags() {
        CheckVerifier.verify(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/tags-noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueOnCorrectlyIndentedRuleBlockTags() {
        CheckVerifier.verifyNoIssues(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/rule-block-tags-compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnIncorrectlyIndentedRuleBlockTags() {
        CheckVerifier.verify(
                new ConsistentIndentationCheck(),
                "checks/consistent-indentation/rule-block-tags-noncompliant.feature");
    }
}
