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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that scenario-level tags within a Rule do not duplicate Rule-level tags.
 * <p>
 * Tags defined on a {@code Rule} block are inherited by all scenarios within
 * that Rule. Repeating them at the scenario level is redundant and should be removed.
 * <p>
 * This check is complementary to {@code no-redundant-tags} (Rule 37), which
 * flags scenario tags duplicating Feature-level tags and also covers Rule-level
 * redundancy. This rule provides a focused, Rule-scoped perspective that teams
 * can enable independently to enforce Rule-level tag hygiene.
 * <p>
 * To avoid overlapping issues with {@code no-redundant-tags}, this check only flags
 * scenario tags that duplicate Rule-level tags and are NOT also present at the
 * Feature level (Feature-level redundancy is exclusively Rule 37's responsibility).
 */
@Rule(key = "no-redundant-rule-tags")
public class NoRedundantRuleTagsCheck extends BaseCheck {

    private Set<String> featureTagNames = new HashSet<>();
    private Set<String> ruleTagNames = new HashSet<>();
    private boolean insideRule;

    @Override
    public void visitFeatureFile(FeatureFile file) {
        featureTagNames.clear();
        ruleTagNames.clear();
        insideRule = false;
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        featureTagNames = feature.tags().stream()
                .map(TagDefinition::name)
                .collect(Collectors.toSet());
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        insideRule = true;
        ruleTagNames = rule.tags().stream()
                .map(TagDefinition::name)
                .collect(Collectors.toSet());
    }

    @Override
    public void leaveRule(RuleDefinition rule) {
        insideRule = false;
        ruleTagNames.clear();
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        if (!insideRule) {
            return;
        }
        for (TagDefinition tag : scenario.tags()) {
            String tagName = tag.name();
            // Only flag tags that duplicate Rule-level tags
            // Skip tags that also exist at Feature level (handled by no-redundant-tags)
            if (ruleTagNames.contains(tagName) && !featureTagNames.contains(tagName)) {
                addIssue(tag.position(),
                        "Remove this redundant tag '" + tagName
                                + "' that is already set at the Rule level.");
            }
        }
    }
}
