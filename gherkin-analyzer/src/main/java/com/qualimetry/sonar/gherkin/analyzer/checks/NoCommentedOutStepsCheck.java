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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import io.cucumber.gherkin.GherkinDialect;
import io.cucumber.gherkin.GherkinDialectProvider;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.Set;

/**
 * Checks for comments that contain Gherkin step keywords, which indicate
 * dead or abandoned behaviour definitions.
 * <p>
 * Step keywords are resolved from the file's Gherkin dialect (the
 * {@code # language:} declaration, defaulting to English), so commented-out
 * steps are detected in any supported language. Commented-out steps should
 * be removed or restored as active steps to keep feature files clean and
 * accurate.
 */
@Rule(key = "no-commented-out-steps")
public class NoCommentedOutStepsCheck extends BaseCheck {

    private static final GherkinDialectProvider DIALECT_PROVIDER = new GherkinDialectProvider();

    @Override
    public void visitFeatureFile(FeatureFile file) {
        String rawContent = getContext().getRawContent();
        if (rawContent == null) {
            return;
        }

        Set<String> stepKeywords = stepKeywordsForLanguage(file.language());

        String[] lines = rawContent.split("\\r?\\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String trimmed = lines[i].trim();
            if (trimmed.startsWith("#")) {
                String commentText = trimmed.substring(1).trim();

                // Exclude language declarations
                if (commentText.startsWith("language:")) {
                    continue;
                }

                for (String keyword : stepKeywords) {
                    if (commentText.startsWith(keyword + " ")
                            || commentText.equals(keyword)) {
                        addLineIssue(i + 1, "Remove or restore this commented-out step.");
                        break;
                    }
                }
            }
        }
    }

    private static Set<String> stepKeywordsForLanguage(String language) {
        GherkinDialect dialect = DIALECT_PROVIDER.getDialect(language)
                .orElse(DIALECT_PROVIDER.getDefaultDialect());
        Set<String> keywords = new HashSet<>();
        for (String keyword : dialect.getStepKeywords()) {
            String trimmed = keyword.trim();
            if (!trimmed.isEmpty()) {
                keywords.add(trimmed);
            }
        }
        return keywords;
    }
}
