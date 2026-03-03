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

class FeatureFileRequiredCheckTest {

    @Test
    void shouldNotRaiseIssueWhenFeaturePresent() {
        CheckVerifier.verifyNoIssues(
                new FeatureFileRequiredCheck(),
                "checks/feature-file-required/compliant.feature");
    }

    @Test
    void shouldRaiseIssueWhenNoFeature() throws IOException {
        // File-level issues cannot be verified via # Noncompliant annotations
        // (they have no line number). Verify manually using the external fixture.
        FeatureFileRequiredCheck check = new FeatureFileRequiredCheck();
        FeatureParser parser = new FeatureParser();

        String content;
        try (InputStream is = getClass().getResourceAsStream(
                "/checks/feature-file-required/noncompliant.feature")) {
            content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        FeatureFile featureFile = parser.parse("noncompliant.feature",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        FeatureContext context = new FeatureContext(featureFile, null, content);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).message())
                .contains("Feature definition");
        assertThat(context.getIssues().get(0).line()).isNull();
    }
}
