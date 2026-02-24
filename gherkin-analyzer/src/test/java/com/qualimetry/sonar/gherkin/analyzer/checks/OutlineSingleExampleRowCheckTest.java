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

class OutlineSingleExampleRowCheckTest {

    @Test
    void shouldNotRaiseIssueOnCompliantFile() {
        CheckVerifier.verifyNoIssues(
                new OutlineSingleExampleRowCheck(),
                "checks/outline-single-example-row/compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnNoncompliantFile() {
        CheckVerifier.verify(
                new OutlineSingleExampleRowCheck(),
                "checks/outline-single-example-row/noncompliant.feature");
    }

    @Test
    void shouldRaiseIssueWithCustomThreshold() {
        OutlineSingleExampleRowCheck check = new OutlineSingleExampleRowCheck();
        check.setMaxDataRows(2);
        CheckVerifier.verify(check,
                "checks/outline-single-example-row/custom-threshold-noncompliant.feature");
    }

    @Test
    void shouldRaiseIssueWhenMultipleExamplesAllSingleRow() {
        CheckVerifier.verify(
                new OutlineSingleExampleRowCheck(),
                "checks/outline-single-example-row/multiple-examples-noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueWhenOneExamplesHasMultipleRows() {
        // One Examples has 1 row, another has 3 rows - NOT all single-row, so no issue.
        CheckVerifier.verifyNoIssues(
                new OutlineSingleExampleRowCheck(),
                "checks/outline-single-example-row/mixed-examples-compliant.feature");
    }
}
