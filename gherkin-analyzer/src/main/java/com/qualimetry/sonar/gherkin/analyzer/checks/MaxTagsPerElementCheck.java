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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.List;

/**
 * Checks that Feature, Rule, Scenario, and Examples elements do not
 * exceed a configurable maximum number of tags.
 * <p>
 * Excessive tags on a single element suggest poor test organization or
 * over-classification. The default limit is 8 tags per element.
 */
@Rule(key = "max-tags-per-element")
public class MaxTagsPerElementCheck extends BaseCheck {

    private static final int DEFAULT_MAX_TAGS = 8;

    @RuleProperty(
            key = "maxTags",
            description = "Maximum number of tags allowed per element",
            defaultValue = "" + DEFAULT_MAX_TAGS)
    private int maxTags = DEFAULT_MAX_TAGS;

    public void setMaxTags(int maxTags) {
        this.maxTags = maxTags;
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        checkTags(feature.tags(), feature.position(), "Feature");
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        checkTags(rule.tags(), rule.position(), "Rule");
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        checkTags(scenario.tags(), scenario.position(), "Scenario");
    }

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        checkTags(examples.tags(), examples.position(), "Examples");
    }

    private void checkTags(List<TagDefinition> tags, TextPosition position, String elementType) {
        if (tags.size() > maxTags) {
            addIssue(position,
                    elementType + " has " + tags.size() + " tags, which exceeds the limit of "
                            + maxTags + ". Reduce the number of tags.");
        }
    }
}
