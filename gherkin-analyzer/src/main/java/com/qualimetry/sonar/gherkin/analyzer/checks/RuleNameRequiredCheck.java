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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that every {@code Rule} block has a non-empty name.
 * <p>
 * A Rule without a name fails to document the business rule it represents.
 * Rule names appear in test reports and help teams understand which business
 * rules are being validated.
 */
@Rule(key = "rule-name-required")
public class RuleNameRequiredCheck extends BaseCheck {

    @Override
    public void visitRule(RuleDefinition rule) {
        if (rule.name() == null || rule.name().isBlank()) {
            addIssue(rule.position(), "Add a name to this Rule.");
        }
    }
}
