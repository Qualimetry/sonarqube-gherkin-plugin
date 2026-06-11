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
 * Represents the Feature definition in a Gherkin feature file.
 * A feature is the top-level grouping of scenarios, backgrounds, and rules.
 *
 * @param position    the position of the Feature keyword in the source file
 * @param keyword     the literal keyword text (e.g. {@code "Feature:"})
 * @param language    the Gherkin language code (e.g. {@code "en"}, {@code "fr"})
 * @param name        the feature name, or empty string
 * @param description the optional description text, or empty string
 * @param tags        the tags applied at the feature level
 * @param background  the optional feature-level background, or {@code null} if none
 * @param scenarios   the scenarios directly under the feature (not inside a Rule)
 * @param rules       the Rule sections within this feature
 */
public record FeatureDefinition(
        TextPosition position,
        String keyword,
        String language,
        String name,
        String description,
        List<TagDefinition> tags,
        BackgroundDefinition background,
        List<ScenarioDefinition> scenarios,
        List<RuleDefinition> rules) {

    public FeatureDefinition {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keyword, "keyword must not be null");
        Objects.requireNonNull(language, "language must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(tags, "tags must not be null");
        Objects.requireNonNull(scenarios, "scenarios must not be null");
        Objects.requireNonNull(rules, "rules must not be null");
        tags = List.copyOf(tags);
        scenarios = List.copyOf(scenarios);
        rules = List.copyOf(rules);
        // background is nullable
    }
}
