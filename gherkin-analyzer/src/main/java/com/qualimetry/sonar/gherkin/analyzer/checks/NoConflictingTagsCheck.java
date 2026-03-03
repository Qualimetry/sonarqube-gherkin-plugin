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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that tags representing mutually exclusive states do not appear
 * together on the same element.
 * <p>
 * For example, {@code @wip} and {@code @release-ready}, or {@code @manual}
 * and {@code @automated}, are contradictory classifications. The user defines
 * conflict pairs via the {@code conflictPairs} property.
 */
@Rule(key = "no-conflicting-tags")
public class NoConflictingTagsCheck extends BaseCheck {

    @RuleProperty(
            key = "conflictPairs",
            description = "Comma-separated conflict pairs using + as separator. "
                    + "Example: wip+release-ready,manual+automated,slow+fast",
            defaultValue = "")
    private String conflictPairs = "";

    private List<String[]> parsedPairs;

    public void setConflictPairs(String conflictPairs) {
        this.conflictPairs = conflictPairs;
        this.parsedPairs = null;
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        checkTags(feature.tags(), feature.position());
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        checkTags(rule.tags(), rule.position());
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        checkTags(scenario.tags(), scenario.position());
    }

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        checkTags(examples.tags(), examples.position());
    }

    private void checkTags(List<TagDefinition> tags, TextPosition position) {
        List<String[]> pairs = getParsedPairs();
        if (pairs.isEmpty()) {
            return;
        }

        Set<String> tagNames = tags.stream()
                .map(TagDefinition::name)
                .collect(Collectors.toSet());

        for (String[] pair : pairs) {
            if (tagNames.contains(pair[0]) && tagNames.contains(pair[1])) {
                addIssue(position,
                        "Tags \"@" + pair[0] + "\" and \"@" + pair[1]
                                + "\" conflict and should not appear together.");
            }
        }
    }

    private List<String[]> getParsedPairs() {
        if (parsedPairs == null) {
            parsedPairs = new ArrayList<>();
            if (conflictPairs != null && !conflictPairs.isBlank()) {
                for (String pair : conflictPairs.split(",")) {
                    String[] parts = pair.trim().split("\\+");
                    if (parts.length == 2
                            && !parts[0].trim().isEmpty()
                            && !parts[1].trim().isEmpty()) {
                        parsedPairs.add(new String[]{parts[0].trim(), parts[1].trim()});
                    }
                }
            }
        }
        return parsedPairs;
    }
}
