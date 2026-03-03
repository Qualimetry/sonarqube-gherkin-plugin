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

import java.util.Objects;

/**
 * Represents a doc string attached to a Gherkin step.
 * Doc strings are delimited by triple-quote ({@code """}) or triple-backtick ({@code ```})
 * markers and may optionally specify a content type (media type hint).
 *
 * @param position    the position of the doc string opening delimiter in the source file
 * @param contentType the optional media type hint (e.g. {@code "json"}), or empty string if none
 * @param content     the body text between the delimiters
 * @param delimiter   the delimiter used ({@code "\"\"\""} or {@code "```"})
 */
public record DocStringDefinition(TextPosition position, String contentType, String content, String delimiter) {

    public DocStringDefinition {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(contentType, "contentType must not be null");
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(delimiter, "delimiter must not be null");
    }
}
