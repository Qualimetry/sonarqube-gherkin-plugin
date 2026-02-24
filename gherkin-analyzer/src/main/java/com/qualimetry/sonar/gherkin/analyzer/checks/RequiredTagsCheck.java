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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;

/**
 * Checks that every Scenario has at least one tag matching a configurable pattern.
 * <p>
 * This is useful for teams that require categorization tags (e.g. {@code @smoke},
 * {@code @regression}) on every scenario. The default pattern {@code .*} matches
 * everything, so the rule is effectively inactive unless configured with a
 * meaningful pattern such as {@code smoke|regression|integration}.
 */
@Rule(key = "required-tags")
public class RequiredTagsCheck extends BaseCheck {

    private static final String DEFAULT_PATTERN = ".*";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression that at least one tag must match (without the leading @)",
            defaultValue = DEFAULT_PATTERN)
    private String pattern = DEFAULT_PATTERN;

    private Pattern compiledPattern;

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = null; // reset cached pattern
    }

    private Pattern getCompiledPattern() {
        if (compiledPattern == null) {
            compiledPattern = Pattern.compile(pattern);
        }
        return compiledPattern;
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        Pattern p = getCompiledPattern();
        boolean hasMatch = false;
        for (TagDefinition tag : scenario.tags()) {
            if (p.matcher(tag.name()).matches()) {
                hasMatch = true;
                break;
            }
        }
        if (!hasMatch) {
            addIssue(scenario.position(),
                    "Add at least one tag matching the required pattern \"" + pattern + "\" to this scenario.");
        }
    }
}
