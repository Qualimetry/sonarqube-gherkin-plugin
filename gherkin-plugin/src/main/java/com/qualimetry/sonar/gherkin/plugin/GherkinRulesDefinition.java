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
package com.qualimetry.sonar.gherkin.plugin;

import com.qualimetry.sonar.gherkin.analyzer.checks.CheckList;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the Gherkin analysis rules repository for SonarQube.
 * <p>
 * Loads all 83 rule check classes from {@link CheckList}, sets their
 * HTML descriptions from classpath resources, configures severities, sets human-readable display names, and assigns constant-per-issue remediation functions. Rules that represent
 * specification errors leading to wrong or failing test runs are classified
 * as {@link RuleType#BUG}; all others are {@link RuleType#CODE_SMELL}.
 */
public class GherkinRulesDefinition implements RulesDefinition {

    private static final String RESOURCE_BASE =
            "/com/qualimetry/sonar/gherkin/analyzer/checks/";

    /**
     * Rule type overrides. Rules not listed default to CODE_SMELL from the annotation loader.
     * BUG: specification is wrong and will cause test failures or wrong runtime behavior.
     */
    private static final Map<String, RuleType> RULE_TYPES = Map.ofEntries(
            Map.entry("parse-error", RuleType.BUG),
            Map.entry("no-unused-variables", RuleType.BUG),
            Map.entry("examples-column-coverage", RuleType.BUG),
            Map.entry("outline-placeholder-required", RuleType.BUG),
            Map.entry("scenario-outline-requires-examples", RuleType.BUG),
            Map.entry("examples-minimum-rows", RuleType.BUG),
            Map.entry("no-empty-examples-cells", RuleType.BUG)
    );

    /**
     * Severity per rule key.
     */
    private static final Map<String, String> SEVERITIES = Map.ofEntries(
            // Structure Rules (1-10)
            Map.entry("feature-file-required", Severity.MAJOR),
            Map.entry("feature-name-required", Severity.CRITICAL),
            Map.entry("feature-description-recommended", Severity.INFO),
            Map.entry("scenario-required", Severity.MAJOR),
            Map.entry("scenario-name-required", Severity.CRITICAL),
            Map.entry("step-required", Severity.MAJOR),
            Map.entry("examples-minimum-rows", Severity.CRITICAL),
            Map.entry("examples-column-coverage", Severity.CRITICAL),
            Map.entry("scenario-count-limit", Severity.MAJOR),
            Map.entry("step-count-limit", Severity.MAJOR),
            // Design Rules (11-21)
            Map.entry("background-given-only", Severity.CRITICAL),
            Map.entry("shared-given-to-background", Severity.MAJOR),
            Map.entry("step-order-given-when-then", Severity.CRITICAL),
            Map.entry("single-when-per-scenario", Severity.MAJOR),
            Map.entry("when-then-required", Severity.CRITICAL),
            Map.entry("unique-feature-name", Severity.MAJOR),
            Map.entry("unique-scenario-name", Severity.MAJOR),
            Map.entry("no-duplicate-steps", Severity.CRITICAL),
            Map.entry("use-scenario-outline-for-examples", Severity.MAJOR),
            Map.entry("business-language-only", Severity.MAJOR),
            Map.entry("consistent-feature-language", Severity.MAJOR),
            // Style and Convention Rules (22-33)
            Map.entry("consistent-indentation", Severity.MINOR),
            Map.entry("no-tab-characters", Severity.MINOR),
            Map.entry("no-trailing-whitespace", Severity.MINOR),
            Map.entry("newline-at-end-of-file", Severity.MINOR),
            Map.entry("no-byte-order-mark", Severity.MAJOR),
            Map.entry("consistent-line-endings", Severity.MINOR),
            Map.entry("file-name-convention", Severity.MINOR),
            Map.entry("comment-format", Severity.MINOR),
            Map.entry("prefer-and-but-keywords", Severity.MINOR),
            Map.entry("no-star-step-prefix", Severity.MINOR),
            Map.entry("examples-separator-line", Severity.MINOR),
            Map.entry("step-sentence-max-length", Severity.MAJOR),
            // Tag Rules (34-38)
            Map.entry("tag-name-pattern", Severity.MINOR),
            Map.entry("tag-permitted-values", Severity.MINOR),
            Map.entry("tag-placement", Severity.MINOR),
            Map.entry("no-redundant-tags", Severity.MINOR),
            Map.entry("no-examples-tags", Severity.MINOR),
            // Variable and Data Rules (39)
            Map.entry("no-unused-variables", Severity.MAJOR),
            // Step Pattern Rules (40-43)
            Map.entry("given-step-pattern", Severity.MINOR),
            Map.entry("when-step-pattern", Severity.MINOR),
            Map.entry("then-step-pattern", Severity.MINOR),
            Map.entry("no-unknown-step-type", Severity.MAJOR),
            // Comment and Marker Rules (44-46)
            Map.entry("todo-comment", Severity.INFO),
            Map.entry("fixme-comment", Severity.INFO),
            Map.entry("comment-pattern-match", Severity.MAJOR),
            // Spelling Rules (47)
            Map.entry("spelling-accuracy", Severity.INFO),
            // Parser Error Rules (48)
            Map.entry("parse-error", Severity.CRITICAL),
            // Rule Block Quality Rules (49-52)
            Map.entry("rule-name-required", Severity.CRITICAL),
            Map.entry("rule-scenario-required", Severity.MAJOR),
            Map.entry("unique-rule-name", Severity.MAJOR),
            Map.entry("rule-description-recommended", Severity.INFO),
            // Structural Integrity Rules (53-56)
            Map.entry("outline-placeholder-required", Severity.MAJOR),
            Map.entry("scenario-outline-requires-examples", Severity.MAJOR),
            Map.entry("background-needs-multiple-scenarios", Severity.MINOR),
            Map.entry("blank-line-before-scenario", Severity.MINOR),
            // Rule-Scoped Best Practices (57-60)
            Map.entry("rule-scenario-count-limit", Severity.MAJOR),
            Map.entry("feature-rule-count-limit", Severity.MAJOR),
            Map.entry("no-redundant-rule-tags", Severity.MINOR),
            Map.entry("rule-tag-placement", Severity.MINOR),
            // Advanced Quality (61-63)
            Map.entry("examples-name-when-multiple", Severity.MINOR),
            Map.entry("consistent-scenario-keyword", Severity.MINOR),
            Map.entry("no-duplicate-tags", Severity.MINOR),
            // Ecosystem Parity (64-69)
            Map.entry("no-multiple-empty-lines", Severity.MINOR),
            Map.entry("required-tags", Severity.MAJOR),
            Map.entry("no-restricted-tags", Severity.MAJOR),
            Map.entry("name-max-length", Severity.MINOR),
            Map.entry("one-space-between-tags", Severity.MINOR),
            Map.entry("no-partially-commented-tag-lines", Severity.MINOR),
            // Configurable Quality Thresholds (70-74)
            Map.entry("outline-single-example-row", Severity.MINOR),
            Map.entry("no-restricted-patterns", Severity.MAJOR),
            Map.entry("max-tags-per-element", Severity.MINOR),
            Map.entry("feature-file-max-lines", Severity.MINOR),
            Map.entry("data-table-max-columns", Severity.MINOR),
            // Rules 75-83
            Map.entry("unique-examples-headers", Severity.CRITICAL),
            Map.entry("no-empty-examples-cells", Severity.MAJOR),
            Map.entry("no-duplicate-scenario-bodies", Severity.MAJOR),
            Map.entry("no-conflicting-tags", Severity.MAJOR),
            Map.entry("no-commented-out-steps", Severity.MINOR),
            Map.entry("background-step-count-limit", Severity.MAJOR),
            Map.entry("feature-name-matches-filename", Severity.MINOR),
            Map.entry("scenario-description-recommended", Severity.INFO),
            Map.entry("no-empty-doc-strings", Severity.MINOR)
    );

    /**
     * Human-readable display names per rule key (Note 75).
     */
    private static final Map<String, String> DISPLAY_NAMES = Map.ofEntries(
            Map.entry("feature-file-required", "Feature File Required"),
            Map.entry("feature-name-required", "Feature Name Required"),
            Map.entry("feature-description-recommended", "Feature Description Recommended"),
            Map.entry("scenario-required", "Scenario Required"),
            Map.entry("scenario-name-required", "Scenario Name Required"),
            Map.entry("step-required", "Step Required"),
            Map.entry("examples-minimum-rows", "Examples Minimum Rows"),
            Map.entry("examples-column-coverage", "Examples Column Coverage"),
            Map.entry("scenario-count-limit", "Scenario Count Limit"),
            Map.entry("step-count-limit", "Step Count Limit"),
            Map.entry("background-given-only", "Background Given Only"),
            Map.entry("shared-given-to-background", "Shared Given to Background"),
            Map.entry("step-order-given-when-then", "Step Order Given-When-Then"),
            Map.entry("single-when-per-scenario", "Single When per Scenario"),
            Map.entry("when-then-required", "When and Then Required"),
            Map.entry("unique-feature-name", "Unique Feature Name"),
            Map.entry("unique-scenario-name", "Unique Scenario Name"),
            Map.entry("no-duplicate-steps", "No Duplicate Steps"),
            Map.entry("use-scenario-outline-for-examples", "Use Scenario Outline for Examples"),
            Map.entry("business-language-only", "Business Language Only"),
            Map.entry("consistent-feature-language", "Consistent Feature Language"),
            Map.entry("consistent-indentation", "Consistent Indentation"),
            Map.entry("no-tab-characters", "No Tab Characters"),
            Map.entry("no-trailing-whitespace", "No Trailing Whitespace"),
            Map.entry("newline-at-end-of-file", "Newline at End of File"),
            Map.entry("no-byte-order-mark", "No Byte Order Mark"),
            Map.entry("consistent-line-endings", "Consistent Line Endings"),
            Map.entry("file-name-convention", "File Name Convention"),
            Map.entry("comment-format", "Comment Format"),
            Map.entry("prefer-and-but-keywords", "Prefer And/But Keywords"),
            Map.entry("no-star-step-prefix", "No Star Step Prefix"),
            Map.entry("examples-separator-line", "Examples Separator Line"),
            Map.entry("step-sentence-max-length", "Step Sentence Max Length"),
            Map.entry("tag-name-pattern", "Tag Name Pattern"),
            Map.entry("tag-permitted-values", "Tag Permitted Values"),
            Map.entry("tag-placement", "Tag Placement"),
            Map.entry("no-redundant-tags", "No Redundant Tags"),
            Map.entry("no-examples-tags", "No Examples Tags"),
            Map.entry("no-unused-variables", "No Unused Variables"),
            Map.entry("given-step-pattern", "Given Step Pattern"),
            Map.entry("when-step-pattern", "When Step Pattern"),
            Map.entry("then-step-pattern", "Then Step Pattern"),
            Map.entry("no-unknown-step-type", "No Unknown Step Type"),
            Map.entry("todo-comment", "TODO Comment"),
            Map.entry("fixme-comment", "FIXME Comment"),
            Map.entry("comment-pattern-match", "Comment Pattern Match"),
            Map.entry("spelling-accuracy", "Spelling Accuracy"),
            Map.entry("parse-error", "Parse Error"),
            // Rule Block Quality Rules (49-52)
            Map.entry("rule-name-required", "Rule Name Required"),
            Map.entry("rule-scenario-required", "Rule Scenario Required"),
            Map.entry("unique-rule-name", "Unique Rule Name"),
            Map.entry("rule-description-recommended", "Rule Description Recommended"),
            // Structural Integrity Rules (53-56)
            Map.entry("outline-placeholder-required", "Outline Placeholder Required"),
            Map.entry("scenario-outline-requires-examples", "Scenario Outline Requires Examples"),
            Map.entry("background-needs-multiple-scenarios", "Background Needs Multiple Scenarios"),
            Map.entry("blank-line-before-scenario", "Blank Line Before Scenario"),
            // Rule-Scoped Best Practices (57-60)
            Map.entry("rule-scenario-count-limit", "Rule Scenario Count Limit"),
            Map.entry("feature-rule-count-limit", "Feature Rule Count Limit"),
            Map.entry("no-redundant-rule-tags", "No Redundant Rule Tags"),
            Map.entry("rule-tag-placement", "Rule Tag Placement"),
            // Advanced Quality (61-63)
            Map.entry("examples-name-when-multiple", "Examples Name When Multiple"),
            Map.entry("consistent-scenario-keyword", "Consistent Scenario Keyword"),
            Map.entry("no-duplicate-tags", "No Duplicate Tags"),
            // Ecosystem Parity (64-69)
            Map.entry("no-multiple-empty-lines", "No Multiple Empty Lines"),
            Map.entry("required-tags", "Required Tags"),
            Map.entry("no-restricted-tags", "No Restricted Tags"),
            Map.entry("name-max-length", "Name Max Length"),
            Map.entry("one-space-between-tags", "One Space Between Tags"),
            Map.entry("no-partially-commented-tag-lines", "No Partially Commented Tag Lines"),
            // Configurable Quality Thresholds (70-74)
            Map.entry("outline-single-example-row", "Outline Single Example Row"),
            Map.entry("no-restricted-patterns", "No Restricted Patterns"),
            Map.entry("max-tags-per-element", "Max Tags Per Element"),
            Map.entry("feature-file-max-lines", "Feature File Max Lines"),
            Map.entry("data-table-max-columns", "Data Table Max Columns"),
            // Rules 75-83
            Map.entry("unique-examples-headers", "Unique Examples Headers"),
            Map.entry("no-empty-examples-cells", "No Empty Examples Cells"),
            Map.entry("no-duplicate-scenario-bodies", "No Duplicate Scenario Bodies"),
            Map.entry("no-conflicting-tags", "No Conflicting Tags"),
            Map.entry("no-commented-out-steps", "No Commented Out Steps"),
            Map.entry("background-step-count-limit", "Background Step Count Limit"),
            Map.entry("feature-name-matches-filename", "Feature Name Matches Filename"),
            Map.entry("scenario-description-recommended", "Scenario Description Recommended"),
            Map.entry("no-empty-doc-strings", "No Empty Doc Strings")
    );

    /**
     * Remediation time per rule key. Most rules use 5min; adjusted
     * for rules where the fix effort is higher or lower.
     */
    private static final Map<String, String> REMEDIATIONS = Map.ofEntries(
            // Quick fixes (1-2 min)
            Map.entry("no-trailing-whitespace", "1min"),
            Map.entry("newline-at-end-of-file", "1min"),
            Map.entry("no-byte-order-mark", "1min"),
            Map.entry("consistent-line-endings", "1min"),
            Map.entry("no-tab-characters", "2min"),
            Map.entry("comment-format", "2min"),
            Map.entry("no-star-step-prefix", "2min"),
            Map.entry("no-examples-tags", "2min"),
            Map.entry("no-redundant-tags", "2min"),
            Map.entry("tag-name-pattern", "2min"),
            Map.entry("tag-permitted-values", "2min"),
            Map.entry("tag-placement", "2min"),
            Map.entry("examples-separator-line", "2min"),
            Map.entry("todo-comment", "10min"),
            Map.entry("fixme-comment", "10min"),
            // Medium fixes (5 min) — default
            Map.entry("feature-file-required", "5min"),
            Map.entry("feature-name-required", "5min"),
            Map.entry("feature-description-recommended", "5min"),
            Map.entry("scenario-required", "5min"),
            Map.entry("scenario-name-required", "5min"),
            Map.entry("step-required", "5min"),
            Map.entry("examples-minimum-rows", "5min"),
            Map.entry("examples-column-coverage", "5min"),
            Map.entry("file-name-convention", "5min"),
            Map.entry("prefer-and-but-keywords", "5min"),
            Map.entry("no-unknown-step-type", "5min"),
            Map.entry("no-unused-variables", "5min"),
            Map.entry("consistent-indentation", "5min"),
            Map.entry("given-step-pattern", "5min"),
            Map.entry("when-step-pattern", "5min"),
            Map.entry("then-step-pattern", "5min"),
            Map.entry("comment-pattern-match", "5min"),
            Map.entry("spelling-accuracy", "5min"),
            Map.entry("parse-error", "5min"),
            // Larger fixes (10-30 min)
            Map.entry("scenario-count-limit", "15min"),
            Map.entry("step-count-limit", "10min"),
            Map.entry("background-given-only", "10min"),
            Map.entry("shared-given-to-background", "15min"),
            Map.entry("step-order-given-when-then", "10min"),
            Map.entry("single-when-per-scenario", "15min"),
            Map.entry("when-then-required", "10min"),
            Map.entry("unique-feature-name", "5min"),
            Map.entry("unique-scenario-name", "5min"),
            Map.entry("no-duplicate-steps", "10min"),
            Map.entry("use-scenario-outline-for-examples", "10min"),
            Map.entry("business-language-only", "10min"),
            Map.entry("consistent-feature-language", "15min"),
            Map.entry("step-sentence-max-length", "10min"),
            // Rule Block Quality Rules (49-52)
            Map.entry("rule-name-required", "5min"),
            Map.entry("rule-scenario-required", "5min"),
            Map.entry("unique-rule-name", "5min"),
            Map.entry("rule-description-recommended", "5min"),
            // Structural Integrity Rules (53-56)
            Map.entry("outline-placeholder-required", "10min"),
            Map.entry("scenario-outline-requires-examples", "5min"),
            Map.entry("background-needs-multiple-scenarios", "10min"),
            Map.entry("blank-line-before-scenario", "2min"),
            // Rule-Scoped Best Practices (57-60)
            Map.entry("rule-scenario-count-limit", "15min"),
            Map.entry("feature-rule-count-limit", "15min"),
            Map.entry("no-redundant-rule-tags", "2min"),
            Map.entry("rule-tag-placement", "2min"),
            // Advanced Quality (61-63)
            Map.entry("examples-name-when-multiple", "5min"),
            Map.entry("consistent-scenario-keyword", "5min"),
            Map.entry("no-duplicate-tags", "2min"),
            // Ecosystem Parity (64-69)
            Map.entry("no-multiple-empty-lines", "1min"),
            Map.entry("required-tags", "5min"),
            Map.entry("no-restricted-tags", "2min"),
            Map.entry("name-max-length", "10min"),
            Map.entry("one-space-between-tags", "1min"),
            Map.entry("no-partially-commented-tag-lines", "2min"),
            // Configurable Quality Thresholds (70-74)
            Map.entry("outline-single-example-row", "10min"),
            Map.entry("no-restricted-patterns", "10min"),
            Map.entry("max-tags-per-element", "5min"),
            Map.entry("feature-file-max-lines", "15min"),
            Map.entry("data-table-max-columns", "10min"),
            // Rules 75-83
            Map.entry("unique-examples-headers", "5min"),
            Map.entry("no-empty-examples-cells", "5min"),
            Map.entry("no-duplicate-scenario-bodies", "15min"),
            Map.entry("no-conflicting-tags", "5min"),
            Map.entry("no-commented-out-steps", "2min"),
            Map.entry("background-step-count-limit", "10min"),
            Map.entry("feature-name-matches-filename", "5min"),
            Map.entry("scenario-description-recommended", "5min"),
            Map.entry("no-empty-doc-strings", "2min")
    );

    @Override
    public void define(Context context) {
        NewRepository repo = context.createRepository(
                CheckList.REPOSITORY_KEY, GherkinLanguage.KEY)
                .setName(CheckList.REPOSITORY_NAME);

        // Load @Rule annotations from all check classes
        new RulesDefinitionAnnotationLoader()
                .load(repo, CheckList.getAllChecks().toArray(new Class<?>[0]));

        // Configure each rule with description, severity, name, and remediation
        for (NewRule rule : repo.rules()) {
            configureRule(rule);
        }

        repo.done();
    }

    private void configureRule(NewRule rule) {
        // Set HTML description from classpath resource
        String htmlPath = RESOURCE_BASE + rule.key() + ".html";
        InputStream descStream = getClass().getResourceAsStream(htmlPath);
        if (descStream != null) {
            String html = readStream(descStream);
            rule.setHtmlDescription(html);
        }

        // Set severity from rule metadata
        String severity = SEVERITIES.get(rule.key());
        if (severity != null) {
            rule.setSeverity(severity);
        }

        // Set rule type (BUG vs CODE_SMELL); default from loader is CODE_SMELL
        RuleType ruleType = RULE_TYPES.get(rule.key());
        if (ruleType != null) {
            rule.setType(ruleType);
        }

        // Set human-readable display name (Note 75)
        String displayName = DISPLAY_NAMES.get(rule.key());
        if (displayName != null) {
            rule.setName(displayName);
        }

        // Set constant-per-issue remediation function
        String remediation = REMEDIATIONS.getOrDefault(rule.key(), "5min");
        rule.setDebtRemediationFunction(
                rule.debtRemediationFunctions().constantPerIssue(remediation));
    }

    private static String readStream(InputStream is) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read rule description", e);
        }
    }
}
