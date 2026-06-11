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
import com.qualimetry.sonar.gherkin.analyzer.visitor.Issue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpellingAccuracyCheckTest {

    @Test
    void shouldNotRaiseIssueOnCompliantFile() {
        CheckVerifier.verifyNoIssues(
                new SpellingAccuracyCheck(),
                "checks/spelling-accuracy/compliant.feature");
    }

    @Test
    void shouldRaiseIssuesOnMisspellings() {
        CheckVerifier.verify(
                new SpellingAccuracyCheck(),
                "checks/spelling-accuracy/noncompliant.feature");
    }

    @Test
    void shouldNotRaiseIssueOnEdgeCaseFile() {
        CheckVerifier.verifyNoIssues(
                new SpellingAccuracyCheck(),
                "checks/spelling-accuracy/edge-case.feature");
    }

    @Test
    void shouldFlagUnknownWordWithoutWordsToIgnore() {
        FeatureContext context = CheckVerifier.walkFixture(
                new SpellingAccuracyCheck(),
                "checks/spelling-accuracy/words-to-ignore.feature");

        assertThat(context.getIssues()).isNotEmpty();
    }

    @Test
    void shouldNotFlagWordsListedInWordsToIgnore() {
        SpellingAccuracyCheck check = new SpellingAccuracyCheck();
        check.setWordsToIgnore("Frobnicator");
        CheckVerifier.verifyNoIssues(check,
                "checks/spelling-accuracy/words-to-ignore.feature");
    }

    @Test
    void shouldReportCorrectLineForMisspellingInMultilineDescription() {
        FeatureContext context = CheckVerifier.walkFixture(
                new SpellingAccuracyCheck(),
                "checks/spelling-accuracy/multiline-description-noncompliant.feature");

        // "procesed" sits on the second description line, which is file line 4.
        assertThat(context.getIssues())
                .extracting(Issue::line)
                .containsExactly(4);
    }
}
