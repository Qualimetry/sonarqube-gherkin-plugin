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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that step sentences do not exceed a maximum length.
 * <p>
 * Long step sentences are hard to read and may indicate that the step
 * is doing too much. Consider splitting long steps into multiple shorter steps.
 */
@Rule(key = "step-sentence-max-length")
public class StepSentenceMaxLengthCheck extends BaseCheck {

    private static final int DEFAULT_MAX_LENGTH = 100;

    @RuleProperty(
            key = "maxLength",
            description = "Maximum allowed length for step sentence text",
            defaultValue = "" + DEFAULT_MAX_LENGTH)
    private int maxLength = DEFAULT_MAX_LENGTH;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void visitStep(StepDefinition step) {
        int textLength = step.text().length();
        if (textLength > maxLength) {
            addIssue(step.position(),
                    "Step sentence is " + textLength + " characters long, which exceeds the maximum "
                            + "of " + maxLength + " characters. Consider splitting it into shorter steps.");
        }
    }
}
