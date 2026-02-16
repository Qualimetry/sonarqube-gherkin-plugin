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

import com.qualimetry.sonar.gherkin.analyzer.parser.FeatureParser;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.testing.CheckVerifier;
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureContext;
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureWalker;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ConsistentLineEndingsCheckTest {

    @Test
    void shouldNotRaiseIssueOnConsistentLfEndings() {
        CheckVerifier.verifyNoIssues(
                new ConsistentLineEndingsCheck(),
                "checks/consistent-line-endings/compliant.feature");
    }

    @Test
    void shouldRaiseIssueOnMixedLineEndings() throws IOException {
        // Mixed line endings cannot be reliably stored in fixture files
        // (editors and git may normalise them). Build content programmatically.
        ConsistentLineEndingsCheck check = new ConsistentLineEndingsCheck();
        FeatureParser parser = new FeatureParser();

        // Read the base fixture, then inject CRLF on selected lines
        String baseContent;
        try (InputStream is = getClass().getResourceAsStream(
                "/checks/consistent-line-endings/noncompliant.feature")) {
            baseContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Replace LF with CRLF on lines 3 and 5 (0-indexed in the split array)
        String[] lines = baseContent.split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i]);
            if (i < lines.length - 1) {
                // Lines 3 and 5 (1-based) get CRLF, others get LF
                if (i == 2 || i == 4) {
                    sb.append("\r\n");
                } else {
                    sb.append("\n");
                }
            }
        }
        String mixedContent = sb.toString();

        FeatureFile featureFile = parser.parse("noncompliant.feature",
                new ByteArrayInputStream(mixedContent.getBytes(StandardCharsets.UTF_8)));

        FeatureContext context = new FeatureContext(featureFile, null, mixedContent);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        // Lines 3 and 5 have CRLF where LF is expected
        assertThat(context.getIssues()).hasSize(2);
        assertThat(context.getIssues().get(0).line()).isEqualTo(3);
        assertThat(context.getIssues().get(1).line()).isEqualTo(5);
    }

    @Test
    void shouldAcceptCrlfWhenConfiguredForCrlf() throws IOException {
        ConsistentLineEndingsCheck check = new ConsistentLineEndingsCheck();
        check.setLineEnding("CRLF");
        FeatureParser parser = new FeatureParser();

        // Build content with consistent CRLF endings
        String crlfContent = "Feature: CRLF test\r\n"
                + "\r\n"
                + "  Scenario: A scenario\r\n"
                + "    Given a precondition\r\n"
                + "    When an action\r\n"
                + "    Then a result\r\n";

        FeatureFile featureFile = parser.parse("crlf-test.feature",
                new ByteArrayInputStream(crlfContent.getBytes(StandardCharsets.UTF_8)));

        FeatureContext context = new FeatureContext(featureFile, null, crlfContent);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        assertThat(context.getIssues()).isEmpty();
    }
}
