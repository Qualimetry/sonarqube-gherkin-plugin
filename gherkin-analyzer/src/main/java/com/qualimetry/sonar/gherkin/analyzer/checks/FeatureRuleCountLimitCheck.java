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
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that Features do not exceed a configurable number of Rule blocks.
 * <p>
 * Features with too many Rules suggest the feature covers too many business
 * domains and should be split into separate feature files.
 */
@Rule(key = "feature-rule-count-limit")
public class FeatureRuleCountLimitCheck extends BaseCheck {

    private static final int DEFAULT_MAX_RULES = 8;

    @RuleProperty(
            key = "maxRules",
            description = "Maximum number of Rule blocks allowed per Feature",
            defaultValue = "" + DEFAULT_MAX_RULES)
    private int maxRules = DEFAULT_MAX_RULES;

    public void setMaxRules(int maxRules) {
        this.maxRules = maxRules;
    }

    @Override
    public void leaveFeature(FeatureDefinition feature) {
        int count = feature.rules().size();
        if (count > maxRules) {
            addIssue(feature.position(),
                    "This Feature has " + count + " Rule blocks, which exceeds the limit of "
                            + maxRules + ". Split it into smaller features.");
        }
    }
}
