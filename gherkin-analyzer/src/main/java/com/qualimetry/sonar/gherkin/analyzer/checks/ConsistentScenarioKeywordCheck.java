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
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import io.cucumber.gherkin.GherkinDialect;
import io.cucumber.gherkin.GherkinDialectProvider;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that all scenarios within a single feature file use the same keyword
 * variant consistently (e.g. all {@code Scenario} or all {@code Example},
 * but not a mix).
 * <p>
 * By default the majority keyword wins and minority usages are flagged.
 * When {@code preferredKeyword} / {@code preferredOutlineKeyword} are set,
 * every scenario (or outline) using a different keyword from the same family
 * is flagged instead. In majority mode, Scenario Outline/Template keywords
 * are excluded because they serve a different purpose.
 */
@Rule(key = "consistent-scenario-keyword")
public class ConsistentScenarioKeywordCheck extends BaseCheck {

    private static final GherkinDialectProvider DIALECT_PROVIDER = new GherkinDialectProvider();

    @RuleProperty(
            key = "preferredKeyword",
            description = "Required keyword for scenarios (e.g. \"Scenario\" or \"Example\"); empty = majority mode",
            defaultValue = "")
    private String preferredKeyword = "";

    @RuleProperty(
            key = "preferredOutlineKeyword",
            description = "Required keyword for scenario outlines (e.g. \"Scenario Outline\"); empty = not checked",
            defaultValue = "")
    private String preferredOutlineKeyword = "";

    private final List<KeywordUsage> keywordUsages = new ArrayList<>();
    private Set<String> scenarioOutlineKeywords = Set.of();

    public void setPreferredKeyword(String preferredKeyword) {
        this.preferredKeyword = preferredKeyword == null ? "" : preferredKeyword.trim();
    }

    public void setPreferredOutlineKeyword(String preferredOutlineKeyword) {
        this.preferredOutlineKeyword = preferredOutlineKeyword == null ? "" : preferredOutlineKeyword.trim();
    }

    private record KeywordUsage(String keyword, String canonicalForm, TextPosition position) {
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        keywordUsages.clear();
        scenarioOutlineKeywords = Set.of();
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        // Resolve the dialect to get outline keywords for exclusion
        String language = feature.language();
        GherkinDialect dialect = DIALECT_PROVIDER.getDialect(language)
                .orElse(DIALECT_PROVIDER.getDialect("en").orElseThrow());
        scenarioOutlineKeywords = dialect.getScenarioOutlineKeywords().stream()
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        if (scenario.keyword() == null) {
            return;
        }

        String keyword = scenario.keyword().trim();

        if (scenarioOutlineKeywords.contains(keyword)) {
            if (!preferredOutlineKeyword.isEmpty() && !keyword.equals(preferredOutlineKeyword)) {
                addIssue(scenario.position(),
                        "Use the configured keyword \"" + preferredOutlineKeyword
                                + "\" instead of \"" + keyword + "\".");
            }
            return;
        }

        if (!preferredKeyword.isEmpty()) {
            if (!keyword.equals(preferredKeyword)) {
                addIssue(scenario.position(),
                        "Use the configured keyword \"" + preferredKeyword
                                + "\" instead of \"" + keyword + "\".");
            }
            return;
        }

        // Canonicalize: resolve localized keyword to a canonical form.
        // In many languages, both "Scenario" and "Example" (and their localized
        // equivalents) are valid keywords for the same construct. We track the
        // raw keyword but group by canonical form for the consistency check.
        // The canonical form is the raw keyword itself since we want to detect
        // mixing of synonyms (e.g., "Scenario" vs "Example").
        String canonicalForm = keyword;
        keywordUsages.add(new KeywordUsage(keyword, canonicalForm, scenario.position()));
    }

    @Override
    public void leaveFeatureFile(FeatureFile file) {
        if (keywordUsages.size() < 2) {
            return;
        }

        // Group usages by canonical keyword form
        Map<String, List<KeywordUsage>> grouped = new HashMap<>();
        for (KeywordUsage usage : keywordUsages) {
            grouped.computeIfAbsent(usage.canonicalForm(), k -> new ArrayList<>()).add(usage);
        }

        if (grouped.size() <= 1) {
            // All scenarios use the same keyword - consistent
            return;
        }

        // Find the majority keyword (most frequently used)
        String majorityKeyword = null;
        int majorityCount = 0;
        for (Map.Entry<String, List<KeywordUsage>> entry : grouped.entrySet()) {
            if (entry.getValue().size() > majorityCount) {
                majorityCount = entry.getValue().size();
                majorityKeyword = entry.getKey();
            }
        }

        // Flag the minority keyword usages
        for (Map.Entry<String, List<KeywordUsage>> entry : grouped.entrySet()) {
            if (!entry.getKey().equals(majorityKeyword)) {
                for (KeywordUsage usage : entry.getValue()) {
                    addIssue(usage.position(),
                            "Use \"" + majorityKeyword + "\" consistently instead of \""
                                    + usage.keyword() + "\"; "
                                    + "the majority of scenarios in this file use \""
                                    + majorityKeyword + "\".");
                }
            }
        }
    }
}
