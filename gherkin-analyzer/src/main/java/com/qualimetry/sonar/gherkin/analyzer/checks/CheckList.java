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

import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;

import java.util.List;

/**
 * Central registry of all 83 Gherkin analysis checks.
 * <p>
 * Provides the repository key, repository name, the full list of check classes,
 * and the list of rule keys that are active in the default quality profile.
 */
public final class CheckList {

    public static final String REPOSITORY_KEY = "qualimetry-gherkin";
    public static final String REPOSITORY_NAME = "Qualimetry Gherkin";

    private CheckList() {
        // utility class
    }

    /**
     * Returns all 83 check classes in registration order.
     */
    public static List<Class<? extends BaseCheck>> getAllChecks() {
        return List.of(
                // Structure Rules (1-10)
                FeatureFileRequiredCheck.class,
                FeatureNameRequiredCheck.class,
                FeatureDescriptionRecommendedCheck.class,
                ScenarioRequiredCheck.class,
                ScenarioNameRequiredCheck.class,
                StepRequiredCheck.class,
                ExamplesMinimumRowsCheck.class,
                ExamplesColumnCoverageCheck.class,
                ScenarioCountLimitCheck.class,
                StepCountLimitCheck.class,
                // Design Rules (11-21)
                BackgroundGivenOnlyCheck.class,
                SharedGivenToBackgroundCheck.class,
                StepOrderGivenWhenThenCheck.class,
                SingleWhenPerScenarioCheck.class,
                WhenThenRequiredCheck.class,
                UniqueFeatureNameCheck.class,
                UniqueScenarioNameCheck.class,
                NoDuplicateStepsCheck.class,
                UseScenarioOutlineForExamplesCheck.class,
                BusinessLanguageOnlyCheck.class,
                ConsistentFeatureLanguageCheck.class,
                // Style and Convention Rules (22-33)
                ConsistentIndentationCheck.class,
                NoTabCharactersCheck.class,
                NoTrailingWhitespaceCheck.class,
                NewlineAtEndOfFileCheck.class,
                NoByteOrderMarkCheck.class,
                ConsistentLineEndingsCheck.class,
                FileNameConventionCheck.class,
                CommentFormatCheck.class,
                PreferAndButKeywordsCheck.class,
                NoStarStepPrefixCheck.class,
                ExamplesSeparatorLineCheck.class,
                StepSentenceMaxLengthCheck.class,
                // Tag Rules (34-38)
                TagNamePatternCheck.class,
                TagPermittedValuesCheck.class,
                TagPlacementCheck.class,
                NoRedundantTagsCheck.class,
                NoExamplesTagsCheck.class,
                // Variable and Data Rules (39)
                NoUnusedVariablesCheck.class,
                // Step Pattern Rules (40-43)
                GivenStepPatternCheck.class,
                WhenStepPatternCheck.class,
                ThenStepPatternCheck.class,
                NoUnknownStepTypeCheck.class,
                // Comment and Marker Rules (44-46)
                TodoCommentCheck.class,
                FixmeCommentCheck.class,
                CommentPatternMatchCheck.class,
                // Spelling Rules (47)
                SpellingAccuracyCheck.class,
                // Parser Error Rules (48)
                ParseErrorCheck.class,
                // Rule Block Quality Rules (49-52)
                RuleNameRequiredCheck.class,
                RuleScenarioRequiredCheck.class,
                UniqueRuleNameCheck.class,
                RuleDescriptionRecommendedCheck.class,
                // Structural Integrity Rules (53-56)
                OutlinePlaceholderRequiredCheck.class,
                ScenarioOutlineRequiresExamplesCheck.class,
                BackgroundNeedsMultipleScenariosCheck.class,
                BlankLineBeforeScenarioCheck.class,
                // Rule-Scoped Best Practices (57-60)
                RuleScenarioCountLimitCheck.class,
                FeatureRuleCountLimitCheck.class,
                NoRedundantRuleTagsCheck.class,
                RuleTagPlacementCheck.class,
                // Advanced Quality (61-63)
                ExamplesNameWhenMultipleCheck.class,
                ConsistentScenarioKeywordCheck.class,
                NoDuplicateTagsCheck.class,
                // Ecosystem Parity (64-69)
                NoMultipleEmptyLinesCheck.class,
                RequiredTagsCheck.class,
                NoRestrictedTagsCheck.class,
                NameMaxLengthCheck.class,
                OneSpaceBetweenTagsCheck.class,
                NoPartiallyCommentedTagLinesCheck.class,
                // Configurable Quality Thresholds (70-74)
                OutlineSingleExampleRowCheck.class,
                NoRestrictedPatternsCheck.class,
                MaxTagsPerElementCheck.class,
                FeatureFileMaxLinesCheck.class,
                DataTableMaxColumnsCheck.class,
                // Rules 75-83
                UniqueExamplesHeadersCheck.class,
                NoEmptyExamplesCellsCheck.class,
                NoDuplicateScenarioBodiesCheck.class,
                NoConflictingTagsCheck.class,
                NoCommentedOutStepsCheck.class,
                BackgroundStepCountLimitCheck.class,
                FeatureNameMatchesFilenameCheck.class,
                ScenarioDescriptionRecommendedCheck.class,
                NoEmptyDocStringsCheck.class
        );
    }

    /**
     * Returns the rule keys that are active in the default quality profile.
     */
    public static List<String> getDefaultRuleKeys() {
        return List.of(
                "feature-file-required",
                "feature-name-required",
                "scenario-required",
                "scenario-name-required",
                "step-required",
                "examples-minimum-rows",
                "examples-column-coverage",
                "scenario-count-limit",
                "step-count-limit",
                "background-given-only",
                "shared-given-to-background",
                "step-order-given-when-then",
                "single-when-per-scenario",
                "when-then-required",
                "unique-feature-name",
                "unique-scenario-name",
                "no-duplicate-steps",
                "use-scenario-outline-for-examples",
                "business-language-only",
                "consistent-indentation",
                "no-tab-characters",
                "no-trailing-whitespace",
                "newline-at-end-of-file",
                "no-byte-order-mark",
                "file-name-convention",
                "comment-format",
                "prefer-and-but-keywords",
                "examples-separator-line",
                "step-sentence-max-length",
                "tag-name-pattern",
                "tag-placement",
                "no-redundant-tags",
                "no-unused-variables",
                "no-unknown-step-type",
                "todo-comment",
                "fixme-comment",
                "parse-error",
                "rule-name-required",
                "rule-scenario-required",
                "unique-rule-name",
                "scenario-outline-requires-examples",
                "blank-line-before-scenario",
                "rule-scenario-count-limit",
                "feature-rule-count-limit",
                "no-redundant-rule-tags",
                "rule-tag-placement",
                "no-duplicate-tags",
                "no-multiple-empty-lines",
                "one-space-between-tags",
                "no-partially-commented-tag-lines",
                // Additional default-active rules (75, 80, 83)
                "unique-examples-headers",
                "background-step-count-limit",
                "no-empty-doc-strings"
        );
    }
}
