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
 * Represents a Background section in a Gherkin feature file.
 * A Background provides steps that are executed before each scenario
 * in the enclosing Feature or Rule.
 *
 * @param position    the position of the Background keyword in the source file
 * @param keyword     the literal keyword text (e.g. {@code "Background:"})
 * @param name        the optional background name, or empty string
 * @param description the optional description text, or empty string
 * @param steps       the ordered list of steps in this background
 */
public record BackgroundDefinition(
        TextPosition position,
        String keyword,
        String name,
        String description,
        List<StepDefinition> steps) {

    public BackgroundDefinition {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keyword, "keyword must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(steps, "steps must not be null");
        steps = List.copyOf(steps);
    }
}
