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

class UniqueFeatureNameCheckTest {

    @Test
    void shouldNotRaiseIssueForUniqueNames() {
        CheckVerifier.verifyNoIssues(
                new UniqueFeatureNameCheck(),
                "checks/unique-feature-name/compliant.feature");
    }

    @Test
    void shouldRaiseIssueForDuplicateFeatureNamesAcrossFiles() throws IOException {
        UniqueFeatureNameCheck check = new UniqueFeatureNameCheck();
        FeatureParser parser = new FeatureParser();

        // First file
        String content1 = readFixture("/checks/unique-feature-name/compliant.feature");
        FeatureFile file1 = parser.parse("file1.feature",
                new ByteArrayInputStream(content1.getBytes(StandardCharsets.UTF_8)));
        FeatureContext ctx1 = new FeatureContext(file1, null, content1);
        check.setContext(ctx1);
        FeatureWalker.walk(file1, check);

        // Second file with same feature name
        String content2 = readFixture("/checks/unique-feature-name/compliant.feature");
        FeatureFile file2 = parser.parse("file2.feature",
                new ByteArrayInputStream(content2.getBytes(StandardCharsets.UTF_8)));
        FeatureContext ctx2 = new FeatureContext(file2, null, content2);
        check.setContext(ctx2);
        FeatureWalker.walk(file2, check);

        // Finalize cross-file check - returns structured results
        List<CrossFileIssue> crossFileIssues = check.afterAllFiles();

        // Should report duplicate on the second file
        assertThat(crossFileIssues).hasSize(1);
        assertThat(crossFileIssues.get(0).uri()).isEqualTo("file2.feature");
        assertThat(crossFileIssues.get(0).message()).contains("already used");
    }

    private String readFixture(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
