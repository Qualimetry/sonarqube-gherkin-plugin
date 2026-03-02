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

class NoByteOrderMarkCheckTest {

    @Test
    void shouldNotRaiseIssueWhenNoBomPresent() {
        CheckVerifier.verifyNoIssues(
                new NoByteOrderMarkCheck(),
                "checks/no-byte-order-mark/compliant.feature");
    }

    @Test
    void shouldRaiseIssueWhenBomPresent() throws IOException {
        // BOM is hard to embed in fixture files - prepend it programmatically.
        NoByteOrderMarkCheck check = new NoByteOrderMarkCheck();
        FeatureParser parser = new FeatureParser();

        String baseContent;
        try (InputStream is = getClass().getResourceAsStream(
                "/checks/no-byte-order-mark/noncompliant.feature")) {
            baseContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Prepend the UTF-8 BOM character
        String contentWithBom = "\uFEFF" + baseContent;

        FeatureFile featureFile = parser.parse("noncompliant.feature",
                new ByteArrayInputStream(contentWithBom.getBytes(StandardCharsets.UTF_8)));

        FeatureContext context = new FeatureContext(featureFile, null, contentWithBom);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).message())
                .contains("BOM");
        assertThat(context.getIssues().get(0).line()).isEqualTo(1);
    }
}
