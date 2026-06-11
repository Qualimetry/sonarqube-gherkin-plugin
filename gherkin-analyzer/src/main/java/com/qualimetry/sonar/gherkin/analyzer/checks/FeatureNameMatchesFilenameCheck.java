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
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.Arrays;

/**
 * Checks that the Feature name broadly corresponds to the file name.
 * <p>
 * Feature names should match their file names to aid discoverability
 * and navigation in large test suites. The comparison uses slugified
 * versions of both names and looks for shared significant words.
 */
@Rule(key = "feature-name-matches-filename")
public class FeatureNameMatchesFilenameCheck extends BaseCheck {

    @Override
    public void visitFeature(FeatureDefinition feature) {
        if (feature.name() == null || feature.name().isBlank()) {
            return;
        }

        String uri = getContext().getFeatureFile().uri();
        String fileName = extractFilename(uri);
        String fileSlug = slugify(stripExtension(fileName));
        String featureSlug = slugify(feature.name());

        String[] featureWords = Arrays.stream(featureSlug.split("-"))
                .filter(w -> w.length() >= 3)
                .toArray(String[]::new);

        if (featureWords.length == 0) {
            return;
        }

        long matchCount = Arrays.stream(featureWords)
                .filter(fileSlug::contains)
                .count();

        if (matchCount == 0) {
            addIssue(feature.position(),
                    "Feature name \"" + feature.name()
                            + "\" does not correspond to file name \"" + fileName + "\".");
        }
    }

    private static String slugify(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private static String stripExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    private static String extractFilename(String uri) {
        String normalized = uri.replace('\\', '/');
        int lastSlash = normalized.lastIndexOf('/');
        return lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
    }
}
