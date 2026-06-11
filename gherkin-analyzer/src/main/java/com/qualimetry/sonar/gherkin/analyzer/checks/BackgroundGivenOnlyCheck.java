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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that {@code Background} sections contain only {@code Given} steps.
 * <p>
 * Background is designed to establish preconditions shared across all scenarios
 * in a Feature. Using When or Then steps in a Background is a design mistake
 * because Background should only set up context, not perform actions or assertions.
 * And/But steps inherit the type of the preceding keyword, so conjunctions
 * following a Given are acceptable while those continuing a When or Then are not.
 */
@Rule(key = "background-given-only")
public class BackgroundGivenOnlyCheck extends BaseCheck {

    @Override
    public void visitBackground(BackgroundDefinition background) {
        String effectiveKeywordType = null;
        for (StepDefinition step : background.steps()) {
            if (!"CONJUNCTION".equals(step.keywordType())) {
                effectiveKeywordType = step.keywordType();
            }
            if ("ACTION".equals(effectiveKeywordType) || "OUTCOME".equals(effectiveKeywordType)) {
                addIssue(step.position(),
                        "Move this " + step.keyword().trim()
                                + " step out of the Background. Only Given steps are allowed here.");
            }
        }
    }
}
