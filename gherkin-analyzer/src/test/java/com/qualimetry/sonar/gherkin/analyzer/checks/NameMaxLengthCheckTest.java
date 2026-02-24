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

class NameMaxLengthCheckTest {

    @Test
    void shouldNotRaiseIssueOnCompliantFile() {
        CheckVerifier.verifyNoIssues(
                new NameMaxLengthCheck(),
                "checks/name-max-length/compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnNoncompliantFile() {
        CheckVerifier.verify(
                new NameMaxLengthCheck(),
                "checks/name-max-length/noncompliant.feature");
    }

    @Test
    void shouldRaiseIssueWithCustomLimit() {
        NameMaxLengthCheck check = new NameMaxLengthCheck();
        check.setMaxLength(50);
        CheckVerifier.verify(check,
                "checks/name-max-length/custom-limit-noncompliant.feature");
    }

    @Test
    void shouldRaiseIssueOnRuleNameExceedingCustomLimit() {
        NameMaxLengthCheck check = new NameMaxLengthCheck();
        check.setMaxLength(50);
        CheckVerifier.verify(check,
                "checks/name-max-length/rule-name-noncompliant.feature");
    }
}
