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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checks that Rule names within a Feature are unique.
 * <p>
 * Duplicate Rule names indicate copy-paste errors or poor feature
 * decomposition. Each Rule should have a unique name that clearly
 * identifies the business rule it represents.
 */
@Rule(key = "unique-rule-name")
public class UniqueRuleNameCheck extends BaseCheck {

    private final Map<String, List<TextPosition>> ruleNameOccurrences = new HashMap<>();

    @Override
    public void visitFeature(FeatureDefinition feature) {
        ruleNameOccurrences.clear();
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        if (rule.name() != null && !rule.name().isBlank()) {
            ruleNameOccurrences
                    .computeIfAbsent(rule.name(), k -> new ArrayList<>())
                    .add(rule.position());
        }
    }

    @Override
    public void leaveFeature(FeatureDefinition feature) {
        for (Map.Entry<String, List<TextPosition>> entry : ruleNameOccurrences.entrySet()) {
            List<TextPosition> positions = entry.getValue();
            if (positions.size() > 1) {
                // Report on the second and subsequent occurrences
                for (int i = 1; i < positions.size(); i++) {
                    addIssue(positions.get(i),
                            "Rename this Rule. The name \"" + entry.getKey()
                                    + "\" is already used in this Feature.");
                }
            }
        }
    }
}
