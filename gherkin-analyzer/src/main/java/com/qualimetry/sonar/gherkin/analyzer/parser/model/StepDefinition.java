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
 * Represents a single step within a scenario or background.
 * <p>
 * The {@code keyword} is the literal keyword text as it appears in the source
 * (e.g. {@code "Given "}, {@code "When "}, {@code "And "}).
 * <p>
 * The {@code keywordType} classifies the semantic meaning of the step,
 * independent of the keyword used. Possible values:
 * <ul>
 *   <li>{@code "CONTEXT"} &ndash; a Given-type step</li>
 *   <li>{@code "ACTION"} &ndash; a When-type step</li>
 *   <li>{@code "OUTCOME"} &ndash; a Then-type step</li>
 *   <li>{@code "CONJUNCTION"} &ndash; an And/But step that continues the previous type</li>
 *   <li>{@code "UNKNOWN"} &ndash; a step whose type cannot be determined (e.g. {@code *})</li>
 * </ul>
 *
 * @param position    the position of the step in the source file
 * @param keyword     the literal keyword text including trailing space
 * @param keywordType the semantic type of the step
 * @param text        the step sentence text (after the keyword)
 * @param dataTable   the optional data table attached to this step, or {@code null}
 * @param docString   the optional doc string attached to this step, or {@code null}
 */
public record StepDefinition(
        TextPosition position,
        String keyword,
        String keywordType,
        String text,
        DataTableDefinition dataTable,
        DocStringDefinition docString) {

    public StepDefinition {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keyword, "keyword must not be null");
        Objects.requireNonNull(keywordType, "keywordType must not be null");
        Objects.requireNonNull(text, "text must not be null");
        // dataTable and docString are nullable
    }
}
