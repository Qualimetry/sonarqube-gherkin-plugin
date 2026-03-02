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
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoMultipleEmptyLinesCheckTest {

    @Test
    void shouldNotRaiseIssueOnCompliantFile() {
        CheckVerifier.verifyNoIssues(
                new NoMultipleEmptyLinesCheck(),
                "checks/no-multiple-empty-lines/compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnNoncompliantFile() {
        NoMultipleEmptyLinesCheck check = new NoMultipleEmptyLinesCheck();
        FeatureContext context = CheckVerifier.walkFixture(check,
                "checks/no-multiple-empty-lines/noncompliant.feature");
        // Fixture has one pair of consecutive blanks at lines 2-3; issue is reported on the extra blank (line 3).
        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).line()).isEqualTo(3);
        assertThat(context.getIssues().get(0).message())
                .isEqualTo("Remove this unnecessary blank line; only one consecutive blank line is allowed.");
    }

    @Test
    void shouldNotRaiseIssueWhenOnlySingleBlanks() {
        CheckVerifier.verifyNoIssues(
                new NoMultipleEmptyLinesCheck(),
                "checks/no-multiple-empty-lines/single-blank-compliant.feature");
    }
}
