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
 * Represents a Scenario or Scenario Outline in a Gherkin feature file.
 * <p>
 * When {@code isOutline} is {@code true}, the scenario is a Scenario Outline
 * and may contain one or more {@link ExamplesDefinition} sections whose data
 * rows parameterize the steps via {@code <variable>} placeholders.
 *
 * @param position    the position of the scenario keyword in the source file
 * @param keyword     the literal keyword text (e.g. {@code "Scenario:"} or {@code "Scenario Outline:"})
 * @param name        the scenario name, or empty string if none
 * @param description the optional description text, or empty string
 * @param tags        the tags applied to this scenario
 * @param steps       the ordered list of steps in this scenario
 * @param examples    the examples sections (non-empty only for Scenario Outlines)
 * @param isOutline   {@code true} if this is a Scenario Outline, {@code false} for a plain Scenario
 */
public record ScenarioDefinition(
        TextPosition position,
        String keyword,
        String name,
        String description,
        List<TagDefinition> tags,
        List<StepDefinition> steps,
        List<ExamplesDefinition> examples,
        boolean isOutline) {

    public ScenarioDefinition {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keyword, "keyword must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(tags, "tags must not be null");
        Objects.requireNonNull(steps, "steps must not be null");
        Objects.requireNonNull(examples, "examples must not be null");
        tags = List.copyOf(tags);
        steps = List.copyOf(steps);
        examples = List.copyOf(examples);
    }
}
