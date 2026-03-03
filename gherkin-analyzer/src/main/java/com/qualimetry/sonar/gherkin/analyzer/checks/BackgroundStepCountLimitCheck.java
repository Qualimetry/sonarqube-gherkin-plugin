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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.BackgroundDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that Background sections do not exceed a configurable number of steps.
 * <p>
 * Every scenario in scope implicitly begins with the Background steps, so a
 * long Background makes scenarios harder to understand and increases the
 * cognitive load on readers and reviewers.
 */
@Rule(key = "background-step-count-limit")
public class BackgroundStepCountLimitCheck extends BaseCheck {

    private static final int DEFAULT_MAX_STEPS = 5;

    @RuleProperty(
            key = "maxSteps",
            description = "Maximum number of steps allowed per Background",
            defaultValue = "" + DEFAULT_MAX_STEPS)
    private int maxSteps = DEFAULT_MAX_STEPS;

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    @Override
    public void visitBackground(BackgroundDefinition background) {
        int stepCount = background.steps().size();
        if (stepCount > maxSteps) {
            addIssue(background.position(),
                    "This Background has " + stepCount + " steps (maximum allowed: "
                            + maxSteps + "). Consider moving some steps into individual scenarios.");
        }
    }
}
