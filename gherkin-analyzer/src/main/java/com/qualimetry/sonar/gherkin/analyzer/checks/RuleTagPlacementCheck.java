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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that tags common to all scenarios within a Rule are placed at the
 * Rule level instead of being repeated on each scenario.
 * <p>
 * This check is complementary to {@code tag-placement} (Rule 36), which handles
 * both Feature-level and Rule-level tag promotion. This rule provides a focused,
 * Rule-scoped perspective that teams can enable independently.
 * <p>
 * To avoid overlapping issues with {@code tag-placement}, this check excludes tags
 * that are already on the Feature level (Feature-level promotion is exclusively
 * Rule 36's responsibility).
 */
@Rule(key = "rule-tag-placement")
public class RuleTagPlacementCheck extends BaseCheck {

    @Override
    public void leaveRule(RuleDefinition rule) {
        List<ScenarioDefinition> scenarios = rule.scenarios();
        if (scenarios.isEmpty()) {
            return;
        }

        // Get Rule-level tag names
        Set<String> ruleTagNames = rule.tags().stream()
                .map(TagDefinition::name)
                .collect(Collectors.toSet());

        // Get Feature-level tag names
        Set<String> featureTagNames = Set.of();
        if (getContext().getFeatureFile().feature() != null) {
            featureTagNames = getContext().getFeatureFile().feature().tags().stream()
                    .map(TagDefinition::name)
                    .collect(Collectors.toSet());
        }

        // Find tags common to ALL scenarios within this Rule
        Set<String> commonTags = findCommonTags(scenarios);

        // Remove tags already on the Rule or Feature
        commonTags.removeAll(ruleTagNames);
        commonTags.removeAll(featureTagNames);

        if (!commonTags.isEmpty()) {
            String tagList = commonTags.stream()
                    .map(tag -> "@" + tag)
                    .sorted()
                    .collect(Collectors.joining(", "));
            addIssue(rule.position(),
                    "Move these tags to the Rule level since they appear on all scenarios within this Rule: "
                            + tagList);
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
