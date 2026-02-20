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
package com.qualimetry.sonar.gherkin.analyzer.parser.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents the root of the parsed Gherkin tree.
 * A {@code FeatureFile} corresponds to a single {@code .feature} file
 * and contains the parsed feature definition (if any), all comments,
 * the detected language, and the file URI.
 *
 * @param feature  the parsed feature definition, or {@code null} if the file
 *                 does not contain a valid Feature
 * @param comments the comments found in the file
 * @param language the Gherkin language code (e.g. {@code "en"}), derived from
 *                 the {@code # language:} declaration or the parser default
 * @param uri      the URI or path identifying this file
 */
public record FeatureFile(
        FeatureDefinition feature,
        List<Comment> comments,
        String language,
        String uri) {

    public FeatureFile {
        Objects.requireNonNull(comments, "comments must not be null");
        Objects.requireNonNull(language, "language must not be null");
        Objects.requireNonNull(uri, "uri must not be null");
        comments = List.copyOf(comments);
        // feature is nullable (file may not contain a Feature definition)
    }
}
