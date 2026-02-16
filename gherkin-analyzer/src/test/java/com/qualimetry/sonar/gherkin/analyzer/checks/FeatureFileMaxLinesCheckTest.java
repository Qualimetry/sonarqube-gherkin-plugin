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

class FeatureFileMaxLinesCheckTest {

    @Test
    void shouldNotRaiseIssueOnCompliantFile() {
        CheckVerifier.verifyNoIssues(
                new FeatureFileMaxLinesCheck(),
                "checks/feature-file-max-lines/compliant.feature");
    }

    @Test
    void shouldRaiseIssueWhenFileExceedsMaxLines() throws IOException {
        // File-level issue — verify manually since addFileIssue has no line number.
        FeatureFileMaxLinesCheck check = new FeatureFileMaxLinesCheck();
        check.setMaxLines(10);

        String content = readFixture("/checks/feature-file-max-lines/noncompliant.feature");

        FeatureParser parser = new FeatureParser();
        FeatureFile featureFile = parser.parse("noncompliant.feature",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        FeatureContext context = new FeatureContext(featureFile, null, content);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).message())
                .contains("exceeds the limit of 10");
        assertThat(context.getIssues().get(0).line()).isNull();
    }

    @Test
    void shouldNotRaiseIssueWhenFileIsWithinLimit() throws IOException {
        FeatureFileMaxLinesCheck check = new FeatureFileMaxLinesCheck();
        check.setMaxLines(500);

        String content = readFixture("/checks/feature-file-max-lines/noncompliant.feature");

        FeatureParser parser = new FeatureParser();
        FeatureFile featureFile = parser.parse("noncompliant.feature",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        FeatureContext context = new FeatureContext(featureFile, null, content);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        assertThat(context.getIssues()).isEmpty();
    }

    @Test
    void shouldRaiseIssueWithCustomLimit() throws IOException {
        FeatureFileMaxLinesCheck check = new FeatureFileMaxLinesCheck();
        check.setMaxLines(5);

        String content = readFixture("/checks/feature-file-max-lines/compliant.feature");

        FeatureParser parser = new FeatureParser();
        FeatureFile featureFile = parser.parse("compliant.feature",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        FeatureContext context = new FeatureContext(featureFile, null, content);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).message())
                .contains("exceeds the limit of 5");
    }

    private String readFixture(String resourcePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
