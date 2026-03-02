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
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import io.cucumber.gherkin.GherkinDialect;
import io.cucumber.gherkin.GherkinDialectProvider;
import org.sonar.check.Rule;

import java.util.List;

/**
 * Checks that Scenario Outlines contain at least one Examples section.
 * <p>
 * A Scenario Outline without Examples produces zero test iterations at runtime
 * and is always a defect. This check uses language-aware keyword matching via
 * {@link GherkinDialectProvider} because the Cucumber parser treats a
 * {@code Scenario Outline:} without Examples as a regular Scenario
 * (empty examples list, {@code isOutline == false}).
 */
@Rule(key = "scenario-outline-requires-examples")
public class ScenarioOutlineRequiresExamplesCheck extends BaseCheck {

    @Override
    public void leaveScenario(ScenarioDefinition scenario) {
        if (scenario.keyword() == null) {
            return;
        }

        // Use language-aware keyword comparison to detect Scenario Outline keywords
        String language = getContext().getFeatureFile().language();
        GherkinDialect dialect = new GherkinDialectProvider().getDialect(language)
                .orElse(new GherkinDialectProvider().getDialect("en").orElseThrow());
        List<String> outlineKeywords = dialect.getScenarioOutlineKeywords();

        boolean isOutlineKeyword = outlineKeywords.stream()
                .anyMatch(ok -> ok.trim().equals(scenario.keyword().trim()));

        if (isOutlineKeyword && scenario.examples().isEmpty()) {
            addIssue(scenario.position(),
                    "Add an Examples section to this Scenario Outline. "
                            + "A Scenario Outline without Examples produces zero test iterations.");
        }
    }
}
