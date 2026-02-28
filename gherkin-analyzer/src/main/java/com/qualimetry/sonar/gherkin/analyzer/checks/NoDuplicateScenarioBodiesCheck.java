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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checks that scenarios within the same scope do not have identical
 * step sequences.
 * <p>
 * Duplicate scenario bodies increase maintenance cost, inflate test
 * counts without adding coverage, and often indicate missed opportunities
 * for Scenario Outline parameterisation or shared Background extraction.
 * Feature-level scenarios are compared among themselves; Rule-scoped
 * scenarios are compared only within the same Rule.
 */
@Rule(key = "no-duplicate-scenario-bodies")
public class NoDuplicateScenarioBodiesCheck extends BaseCheck {

    private final List<SignatureEntry> featureLevelSignatures = new ArrayList<>();
    private final List<SignatureEntry> ruleLevelSignatures = new ArrayList<>();
    private boolean insideRule;

    private record SignatureEntry(List<String> signature, TextPosition position) {
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        featureLevelSignatures.clear();
        ruleLevelSignatures.clear();
        insideRule = false;
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        insideRule = true;
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        if (scenario.steps().isEmpty()) {
            return;
        }

        List<String> signature = scenario.steps().stream()
                .map(s -> s.keyword().trim() + " " + s.text().trim())
                .toList();

        SignatureEntry entry = new SignatureEntry(signature, scenario.position());
        if (insideRule) {
            ruleLevelSignatures.add(entry);
        } else {
            featureLevelSignatures.add(entry);
        }
    }

    @Override
    public void leaveRule(RuleDefinition rule) {
        checkForDuplicates(ruleLevelSignatures);
        ruleLevelSignatures.clear();
        insideRule = false;
    }

    @Override
    public void leaveFeature(FeatureDefinition feature) {
        checkForDuplicates(featureLevelSignatures);
        featureLevelSignatures.clear();
    }

    private void checkForDuplicates(List<SignatureEntry> entries) {
        Map<List<String>, TextPosition> seen = new HashMap<>();
        for (SignatureEntry entry : entries) {
            TextPosition firstOccurrence = seen.get(entry.signature());
            if (firstOccurrence != null) {
                addIssue(entry.position(),
                        "This scenario has an identical step sequence to the scenario at line "
                                + firstOccurrence.line() + ". Consider consolidating.");
            } else {
                seen.put(entry.signature(), entry.position());
            }
        }
    }
}
