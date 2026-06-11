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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that tags common to all scenarios are placed at the Feature level
 * instead of being repeated on each scenario.
 * <p>
 * Tags that appear on every scenario within a feature should be moved to the
 * feature level to reduce duplication.
 * <p>
 * Rule-level tag promotion is handled by the companion rule
 * {@code rule-tag-placement} (Rule 60), which suggests moving tags common
 * to all scenarios within a Rule to the Rule level. This separation ensures
 * clean single-scope ownership: this rule handles Feature-level, Rule 60
 * handles Rule-level.
 */
@Rule(key = "tag-placement")
public class TagPlacementCheck extends BaseCheck {

    @Override
    public void leaveFeature(FeatureDefinition feature) {
        List<ScenarioDefinition> allScenarios = new ArrayList<>(feature.scenarios());

        // Collect scenarios from rules as well
        feature.rules().forEach(rule -> allScenarios.addAll(rule.scenarios()));

        if (allScenarios.isEmpty()) {
            return;
        }

        // Only suggest Feature-level promotion when there are Feature-level
        // scenarios or multiple Rules. When all scenarios are in a single Rule,
        // rule-tag-placement (Rule 60) handles promotion at the Rule level.
        boolean hasFeatureLevelScenarios = !feature.scenarios().isEmpty();
        boolean hasMultipleRules = feature.rules().size() > 1;
        if (!hasFeatureLevelScenarios && !hasMultipleRules) {
            return;
        }

        // Get feature-level tag names
        Set<String> featureTagNames = feature.tags().stream()
                .map(TagDefinition::name)
                .collect(Collectors.toSet());

        // Find tags that appear on ALL scenarios
        Set<String> commonTags = findCommonTags(allScenarios);

        // Report tags that are common to all scenarios but not on the feature
        commonTags.removeAll(featureTagNames);

        if (!commonTags.isEmpty()) {
            String tagList = commonTags.stream()
                    .map(tag -> "@" + tag)
                    .sorted()
                    .collect(Collectors.joining(", "));
            addLineIssue(feature.position().line(),
                    "Move these tags to the Feature level since they appear on all scenarios: " + tagList);
        }
    }

    private Set<String> findCommonTags(List<ScenarioDefinition> scenarios) {
        Set<String> commonTags = null;
        for (ScenarioDefinition scenario : scenarios) {
            Set<String> scenarioTagNames = scenario.tags().stream()
                    .map(TagDefinition::name)
                    .collect(Collectors.toSet());

            if (commonTags == null) {
                commonTags = new HashSet<>(scenarioTagNames);
            } else {
                commonTags.retainAll(scenarioTagNames);
            }
        }
        return commonTags == null ? new HashSet<>() : commonTags;
    }
}
