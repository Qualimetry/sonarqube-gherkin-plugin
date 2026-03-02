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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that Feature, Scenario, and Rule names do not exceed a configurable maximum length.
 * <p>
 * Excessively long names are hard to read in reports, IDE tooltips, and CI logs.
 * The default threshold is 120 characters.
 */
@Rule(key = "name-max-length")
public class NameMaxLengthCheck extends BaseCheck {

    private static final int DEFAULT_MAX_LENGTH = 120;

    @RuleProperty(
            key = "maxLength",
            description = "Maximum allowed length for Feature, Scenario, and Rule names",
            defaultValue = "" + DEFAULT_MAX_LENGTH)
    private int maxLength = DEFAULT_MAX_LENGTH;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        checkName(feature.name(), feature.position(), "Feature");
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        checkName(scenario.name(), scenario.position(), "Scenario");
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        checkName(rule.name(), rule.position(), "Rule");
    }

    private void checkName(String name, TextPosition position, String elementType) {
        if (name.length() > maxLength) {
            addIssue(position,
                    elementType + " name is " + name.length() + " characters long, which exceeds the limit of "
                            + maxLength + ". Shorten the name.");
        }
    }
}
