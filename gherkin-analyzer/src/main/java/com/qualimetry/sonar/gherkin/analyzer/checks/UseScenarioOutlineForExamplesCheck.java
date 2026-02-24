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
 * Checks that Examples tables appear only in Scenario Outlines.
 * <p>
 * The Gherkin specification defines that Examples tables are used with
 * Scenario Outline to parameterize test execution. This check uses
 * language-aware keyword comparison via {@link GherkinDialectProvider}
 * to correctly handle non-English Gherkin files (e.g., German
 * {@code Szenariogrundriss}, French {@code Plan du scénario}).
 */
@Rule(key = "use-scenario-outline-for-examples")
public class UseScenarioOutlineForExamplesCheck extends BaseCheck {

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        if (scenario.examples().isEmpty() || scenario.keyword() == null) {
            return;
        }

        // Use language-aware keyword comparison
        String language = getContext().getFeatureFile().language();
        GherkinDialect dialect = new GherkinDialectProvider().getDialect(language)
                .orElse(new GherkinDialectProvider().getDialect("en").orElseThrow());
        List<String> outlineKeywords = dialect.getScenarioOutlineKeywords();

        boolean isOutlineKeyword = outlineKeywords.stream()
                .anyMatch(ok -> ok.trim().equals(scenario.keyword().trim()));

        if (!isOutlineKeyword) {
            addIssue(scenario.position(),
                    "Use \"Scenario Outline\" instead of \"Scenario\" when an Examples table is present.");
        }
    }
}
