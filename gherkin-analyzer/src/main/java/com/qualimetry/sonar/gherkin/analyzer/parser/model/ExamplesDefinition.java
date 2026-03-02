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
 * Represents an Examples section within a Scenario Outline.
 * An Examples section provides a data table whose rows are used to
 * parameterize the steps of the enclosing Scenario Outline.
 *
 * @param position    the position of the Examples keyword in the source file
 * @param keyword     the literal keyword text (e.g. {@code "Examples:"})
 * @param name        the optional name of the examples section, or empty string
 * @param description the optional description text, or empty string
 * @param tags        the tags applied to this examples section
 * @param table       the data table containing header and data rows, or {@code null} if absent
 */
public record ExamplesDefinition(
        TextPosition position,
        String keyword,
        String name,
        String description,
        List<TagDefinition> tags,
        DataTableDefinition table) {

    public ExamplesDefinition {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keyword, "keyword must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(tags, "tags must not be null");
        tags = List.copyOf(tags);
        // table is nullable
    }
}
