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
 * Represents a data table attached to a step or an examples section.
 * Each row is a list of cell values (strings). The first row is typically the header.
 *
 * @param position the position of the data table in the source file
 * @param rows     the table rows; each row is an immutable list of cell values
 */
public record DataTableDefinition(TextPosition position, List<List<String>> rows) {

    public DataTableDefinition {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(rows, "rows must not be null");
        rows = rows.stream()
                .map(List::copyOf)
                .toList();
    }
}
