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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks that the same tag does not appear more than once on a single element.
 * <p>
 * Duplicate tags on the same Feature, Rule, Scenario, or Examples section
 * are always redundant. Unlike {@code no-redundant-tags} (which detects tags
 * inherited from a parent element), this check catches exact duplicates
 * on the same element's own tag list.
 */
@Rule(key = "no-duplicate-tags")
public class NoDuplicateTagsCheck extends BaseCheck {

    @Override
    public void visitFeature(FeatureDefinition feature) {
        checkForDuplicates(feature.tags());
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        checkForDuplicates(rule.tags());
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        checkForDuplicates(scenario.tags());
    }

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        checkForDuplicates(examples.tags());
    }

    private void checkForDuplicates(List<TagDefinition> tags) {
        Set<String> seen = new HashSet<>();
        for (TagDefinition tag : tags) {
            if (!seen.add(tag.name())) {
                addIssue(tag.position(),
                        "Remove this duplicate tag \"@" + tag.name()
                                + "\"; it already appears on this element.");
            }
        }
    }
}
