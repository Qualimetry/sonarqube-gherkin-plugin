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
import com.qualimetry.sonar.gherkin.analyzer.visitor.CrossFileIssue;
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureContext;
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureWalker;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsistentFeatureLanguageCheckTest {

    @Test
    void shouldNotRaiseIssueForSingleFile() {
        CheckVerifier.verifyNoIssues(
                new ConsistentFeatureLanguageCheck(),
                "checks/consistent-feature-language/compliant.feature");
    }

    @Test
    void shouldRaiseIssueWhenLanguagesDiffer() throws IOException {
        ConsistentFeatureLanguageCheck check = new ConsistentFeatureLanguageCheck();
        FeatureParser parser = new FeatureParser();

        // English file
        String content1 = readFixture("/checks/consistent-feature-language/compliant.feature");
        FeatureFile file1 = parser.parse("english.feature",
                new ByteArrayInputStream(content1.getBytes(StandardCharsets.UTF_8)));
        FeatureContext ctx1 = new FeatureContext(file1, null, content1);
        check.setContext(ctx1);
        FeatureWalker.walk(file1, check);

        // French file
        String content2 = readFixture("/checks/consistent-feature-language/noncompliant.feature");
        FeatureFile file2 = parser.parse("french.feature",
                new ByteArrayInputStream(content2.getBytes(StandardCharsets.UTF_8)));
        FeatureContext ctx2 = new FeatureContext(file2, null, content2);
        check.setContext(ctx2);
        FeatureWalker.walk(file2, check);

        List<CrossFileIssue> crossFileIssues = check.afterAllFiles();

        assertThat(crossFileIssues).isNotEmpty();
        assertThat(crossFileIssues.get(0).message()).contains("consistency");
    }

    @Test
    void shouldNotRaiseIssueWhenMultipleFilesUseSameLanguage() throws IOException {
        ConsistentFeatureLanguageCheck check = new ConsistentFeatureLanguageCheck();
        FeatureParser parser = new FeatureParser();

        // Two English files
        String content1 = readFixture("/checks/consistent-feature-language/compliant.feature");
        FeatureFile file1 = parser.parse("english1.feature",
                new ByteArrayInputStream(content1.getBytes(StandardCharsets.UTF_8)));
        check.setContext(new FeatureContext(file1, null, content1));
        FeatureWalker.walk(file1, check);

        String content2 = readFixture("/checks/consistent-feature-language/english-second.feature");
        FeatureFile file2 = parser.parse("english2.feature",
                new ByteArrayInputStream(content2.getBytes(StandardCharsets.UTF_8)));
        FeatureContext ctx2 = new FeatureContext(file2, null, content2);
        check.setContext(ctx2);
        FeatureWalker.walk(file2, check);

        List<CrossFileIssue> crossFileIssues = check.afterAllFiles();

        assertThat(crossFileIssues).isEmpty();
    }

    @Test
    void shouldNotRaiseIssueWhenMultipleFrenchFilesAreConsistent() throws IOException {
        ConsistentFeatureLanguageCheck check = new ConsistentFeatureLanguageCheck();
        FeatureParser parser = new FeatureParser();

        // Two French files
        String content1 = readFixture("/checks/consistent-feature-language/noncompliant.feature");
        FeatureFile file1 = parser.parse("french1.feature",
                new ByteArrayInputStream(content1.getBytes(StandardCharsets.UTF_8)));
        check.setContext(new FeatureContext(file1, null, content1));
        FeatureWalker.walk(file1, check);

        String content2 = readFixture("/checks/consistent-feature-language/french-compliant.feature");
        FeatureFile file2 = parser.parse("french2.feature",
                new ByteArrayInputStream(content2.getBytes(StandardCharsets.UTF_8)));
        FeatureContext ctx2 = new FeatureContext(file2, null, content2);
        check.setContext(ctx2);
        FeatureWalker.walk(file2, check);

        List<CrossFileIssue> crossFileIssues = check.afterAllFiles();

        assertThat(crossFileIssues).isEmpty();
    }

    @Test
    void shouldProduceSameIssuesRegardlessOfVisitOrder() throws IOException {
        String englishContent = readFixture("/checks/consistent-feature-language/compliant.feature");
        String frenchContent = readFixture("/checks/consistent-feature-language/noncompliant.feature");

        List<CrossFileIssue> englishFirst = runCheck(
                List.of("a-english.feature", "b-french.feature"),
                List.of(englishContent, frenchContent));
        List<CrossFileIssue> frenchFirst = runCheck(
                List.of("b-french.feature", "a-english.feature"),
                List.of(frenchContent, englishContent));

        assertThat(englishFirst).hasSize(1);
        assertThat(englishFirst.get(0).uri()).isEqualTo("b-french.feature");
        assertThat(frenchFirst).usingRecursiveComparison().isEqualTo(englishFirst);
    }

    private List<CrossFileIssue> runCheck(List<String> uris, List<String> contents) throws IOException {
        ConsistentFeatureLanguageCheck check = new ConsistentFeatureLanguageCheck();
        FeatureParser parser = new FeatureParser();
        for (int i = 0; i < uris.size(); i++) {
            FeatureFile file = parser.parse(uris.get(i),
                    new ByteArrayInputStream(contents.get(i).getBytes(StandardCharsets.UTF_8)));
            check.setContext(new FeatureContext(file, null, contents.get(i)));
            FeatureWalker.walk(file, check);
        }
        return check.afterAllFiles();
    }

    private String readFixture(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
