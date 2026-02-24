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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that consecutive steps of the same type use And/But keywords instead
 * of repeating the same keyword (Given, When, or Then).
 * <p>
 * When multiple steps share the same semantic type (CONTEXT, ACTION, or OUTCOME),
 * subsequent steps should use conjunction keywords (And/But) rather than
 * repeating the primary keyword.
 */
@Rule(key = "prefer-and-but-keywords")
public class PreferAndButKeywordsCheck extends BaseCheck {

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        String previousKeywordType = null;

        for (StepDefinition step : scenario.steps()) {
            String keywordType = step.keywordType();

            // Skip UNKNOWN and CONJUNCTION types - they don't establish a new type
            if ("UNKNOWN".equals(keywordType) || "CONJUNCTION".equals(keywordType)) {
                continue;
            }

            // If this step has the same keywordType as the previous step,
            // it should use And/But instead
            if (previousKeywordType != null && previousKeywordType.equals(keywordType)) {
                String keywordName = step.keyword().trim();
                addIssue(step.position(),
                        "This " + keywordName + " step has the same type as the previous step. "
                                + "Use 'And' or 'But' instead of repeating '" + keywordName + "'.");
            }

            previousKeywordType = keywordType;
        }
    }
}
