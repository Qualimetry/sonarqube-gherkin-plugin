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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import com.qualimetry.sonar.gherkin.analyzer.visitor.CrossFileIssue;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that all features use the same Gherkin language.
 * <p>
 * Mixing languages within a project (e.g., some features in English and
 * others in French) reduces readability and consistency. All features
 * should use the same language unless there is a specific reason to
 * vary. The first language encountered is treated as the expected one.
 * <p>
 * This is a cross-file check that returns structured {@link CrossFileIssue}
 * results from {@link #afterAllFiles()}.
 */
@Rule(key = "consistent-feature-language")
public class ConsistentFeatureLanguageCheck extends BaseCheck {

    private String expectedLanguage;
    private final List<LanguageOccurrence> occurrences = new ArrayList<>();

    private record LanguageOccurrence(String language, String uri, TextPosition position) {}

    @Override
    public void visitFeature(FeatureDefinition feature) {
        String language = feature.language();
        if (language == null) {
            language = "en";
        }
        String uri = getContext().getFeatureFile().uri();
        occurrences.add(new LanguageOccurrence(language, uri, feature.position()));

        if (expectedLanguage == null) {
            expectedLanguage = language;
        }
    }

    /**
     * Called after all files have been processed. Returns structured
     * cross-file issues for language inconsistencies.
     *
     * @return list of cross-file issues with correct file URIs
     */
    public List<CrossFileIssue> afterAllFiles() {
        List<CrossFileIssue> issues = new ArrayList<>();
        if (expectedLanguage == null) {
            return issues;
        }
        for (LanguageOccurrence occ : occurrences) {
            if (!expectedLanguage.equals(occ.language())) {
                issues.add(new CrossFileIssue(
                        getRuleKey(),
                        occ.uri(),
                        occ.position().line(),
                        "Use the language \"" + expectedLanguage
                                + "\" for consistency. This Feature uses \"" + occ.language() + "\"."));
            }
        }
        return issues;
    }
}
