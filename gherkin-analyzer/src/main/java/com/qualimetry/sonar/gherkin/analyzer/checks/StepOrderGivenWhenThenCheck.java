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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that steps follow the Given-When-Then order within each scenario.
 * <p>
 * The Gherkin specification defines a natural ordering: Given (context),
 * When (action), Then (outcome). Steps should not go backwards in this
 * progression. And/But (conjunction) steps inherit the type of the
 * preceding keyword.
 */
@Rule(key = "step-order-given-when-then")
public class StepOrderGivenWhenThenCheck extends BaseCheck {

    private static final int PHASE_GIVEN = 0;
    private static final int PHASE_WHEN = 1;
    private static final int PHASE_THEN = 2;

    private int currentPhase;

    @Override
    public void visitFeatureFile(FeatureFile file) {
        currentPhase = PHASE_GIVEN;
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        currentPhase = PHASE_GIVEN;
        for (StepDefinition step : scenario.steps()) {
            int stepPhase = phaseOf(step.keywordType());
            if (stepPhase < 0) {
                continue; // UNKNOWN or CONJUNCTION inherit current phase
            }
            if (stepPhase < currentPhase) {
                addIssue(step.position(),
                        "Unexpected " + step.keyword().trim()
                                + " step. Reorder the steps of this scenario to follow Given/When/Then order.");
            } else {
                currentPhase = stepPhase;
            }
        }
    }

    private static int phaseOf(String keywordType) {
        return switch (keywordType) {
            case "CONTEXT" -> PHASE_GIVEN;
            case "ACTION" -> PHASE_WHEN;
            case "OUTCOME" -> PHASE_THEN;
            default -> -1; // CONJUNCTION and UNKNOWN don't change phase
        };
    }
}
