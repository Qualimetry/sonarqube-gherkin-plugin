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
 * Represents a Rule section in a Gherkin feature file.
 * The {@code Rule} keyword (introduced in Gherkin 6) groups related scenarios
 * under a business rule within a feature.
 *
 * @param position    the position of the Rule keyword in the source file
 * @param keyword     the literal keyword text (e.g. {@code "Rule:"})
 * @param name        the rule name, or empty string
 * @param description the optional description text, or empty string
 * @param tags        the tags applied to this rule section
 * @param background  the optional background for this rule, or {@code null} if none
 * @param scenarios   the scenarios contained within this rule
 */
public record RuleDefinition(
        TextPosition position,
        String keyword,
        String name,
        String description,
        List<TagDefinition> tags,
        BackgroundDefinition background,
        List<ScenarioDefinition> scenarios) {

    public RuleDefinition {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keyword, "keyword must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(tags, "tags must not be null");
        Objects.requireNonNull(scenarios, "scenarios must not be null");
        tags = List.copyOf(tags);
        scenarios = List.copyOf(scenarios);
        // background is nullable
    }
}
