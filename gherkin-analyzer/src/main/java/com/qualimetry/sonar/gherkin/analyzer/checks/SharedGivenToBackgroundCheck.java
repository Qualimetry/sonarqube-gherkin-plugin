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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks that Given steps common to all scenarios are moved to the Background.
 * <p>
 * Scopes the analysis per container (Feature or Rule) independently. When every
 * scenario within a container starts with the same Given step(s), those steps
 * should be extracted into a Background section to reduce duplication. Feature-level
 * analysis excludes Rule-scoped scenarios. Rule-level analysis checks within each
 * Rule independently.
 */
@Rule(key = "shared-given-to-background")
public class SharedGivenToBackgroundCheck extends BaseCheck {

    private List<List<String>> featureLevelGivens;
    private List<List<String>> ruleLevelGivens;
    private boolean insideRule;

    @Override
    public void visitFeatureFile(FeatureFile file) {
        featureLevelGivens = new ArrayList<>();
        ruleLevelGivens = new ArrayList<>();
        insideRule = false;
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        List<String> givenSteps = new ArrayList<>();
        for (StepDefinition step : scenario.steps()) {
            String keywordType = step.keywordType();
            if ("CONTEXT".equals(keywordType) || "CONJUNCTION".equals(keywordType)) {
                givenSteps.add(step.text());
            } else {
                break; // stop at the first non-Given step
            }
        }
        if (insideRule) {
            ruleLevelGivens.add(givenSteps);
        } else {
            featureLevelGivens.add(givenSteps);
        }
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        insideRule = true;
        ruleLevelGivens = new ArrayList<>();
    }

    @Override
    public void leaveRule(RuleDefinition rule) {
        // Check for common Given steps within this Rule
        if (rule.background() == null && ruleLevelGivens.size() >= 2) {
            Set<String> commonGivens = findCommonGivens(ruleLevelGivens);
            if (!commonGivens.isEmpty()) {
                addIssue(rule.position(),
                        "Move the common Given step(s) to a Background section within this Rule.");
            }
        }
        insideRule = false;
    }

    @Override
    public void leaveFeature(FeatureDefinition feature) {
        // Only suggest if there's no feature-level background and at least 2 feature-level scenarios
        if (feature.background() != null || featureLevelGivens.size() < 2) {
            return;
        }

        Set<String> commonGivens = findCommonGivens(featureLevelGivens);
        if (!commonGivens.isEmpty()) {
            addIssue(feature.position(),
                    "Move the common Given step(s) to a Background section.");
        }
    }

    private Set<String> findCommonGivens(List<List<String>> allGivens) {
        if (allGivens.stream().anyMatch(List::isEmpty)) {
            return Set.of(); // at least one scenario has no Given steps
        }

        Set<String> common = new LinkedHashSet<>(allGivens.get(0));
        for (int i = 1; i < allGivens.size(); i++) {
            common.retainAll(new LinkedHashSet<>(allGivens.get(i)));
        }
        return common;
    }
}
