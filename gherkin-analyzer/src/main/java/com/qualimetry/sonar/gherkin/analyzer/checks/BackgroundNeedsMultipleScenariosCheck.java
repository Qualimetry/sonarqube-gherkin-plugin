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
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that a Background section serves more than one scenario in its scope.
 * <p>
 * A Background is unnecessary when there is only one scenario in its scope
 * (Feature or Rule). The Background steps should be inlined into the single
 * scenario for clarity.
 * <p>
 * The Feature-level check uses a {@code feature.rules().isEmpty()} guard to
 * avoid incorrectly flagging Feature-level Backgrounds that serve Rule-scoped
 * scenarios (Note 125).
 */
@Rule(key = "background-needs-multiple-scenarios")
public class BackgroundNeedsMultipleScenariosCheck extends BaseCheck {

    @Override
    public void leaveFeature(FeatureDefinition feature) {
        // Only check Feature-level Background when there are no Rules.
        // If Rules exist, the Feature Background may serve scenarios inside Rules.
        if (feature.background() != null
                && feature.rules().isEmpty()
                && feature.scenarios().size() == 1) {
            addIssue(feature.background().position(),
                    "This Background serves only one scenario. "
                            + "Inline the Background steps into the scenario for clarity.");
        }
    }

    @Override
    public void leaveRule(RuleDefinition rule) {
        if (rule.background() != null && rule.scenarios().size() == 1) {
            addIssue(rule.background().position(),
                    "This Background serves only one scenario within this Rule. "
                            + "Inline the Background steps into the scenario for clarity.");
        }
    }
}
