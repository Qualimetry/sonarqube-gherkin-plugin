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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that scenario-level tags do not duplicate feature-level tags.
 * <p>
 * Tags defined at the Feature level automatically apply to all scenarios
 * within that feature. Repeating them at the scenario level is redundant
 * and should be removed.
 * <p>
 * Rule-level tag redundancy is handled by the companion rule
 * {@code no-redundant-rule-tags} (Rule 59), which checks scenario tags
 * against Rule-level inherited tags. This separation ensures clean
 * single-scope ownership: this rule handles Feature-level, Rule 59
 * handles Rule-level.
 */
@Rule(key = "no-redundant-tags")
public class NoRedundantTagsCheck extends BaseCheck {

    private Set<String> featureTagNames = new HashSet<>();

    @Override
    public void visitFeatureFile(FeatureFile file) {
        featureTagNames.clear();
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        featureTagNames = feature.tags().stream()
                .map(TagDefinition::name)
                .collect(Collectors.toSet());
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        for (TagDefinition tag : scenario.tags()) {
            String tagName = tag.name();
            if (featureTagNames.contains(tagName)) {
                addIssue(tag.position(),
                        "Remove this redundant tag '" + tagName
                                + "' that is already set at the Feature level.");
            }
        }
    }
}
