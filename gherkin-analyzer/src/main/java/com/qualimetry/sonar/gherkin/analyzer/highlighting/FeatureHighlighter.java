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
package com.qualimetry.sonar.gherkin.analyzer.highlighting;

import com.qualimetry.sonar.gherkin.analyzer.parser.model.BackgroundDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.Comment;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Produces syntax highlighting ranges for a parsed {@link FeatureFile}.
 * <p>
 * Highlights:
 * <ul>
 *   <li>Gherkin keywords ({@code Feature}, {@code Scenario}, {@code Given},
 *       {@code When}, {@code Then}, {@code And}, {@code But}, {@code Background},
 *       {@code Scenario Outline}, {@code Examples}, {@code Rule}) as {@link HighlightType#KEYWORD}</li>
 *   <li>Tags ({@code @tagname}) as {@link HighlightType#ANNOTATION}</li>
 *   <li>Comments ({@code # text}) as {@link HighlightType#COMMENT}</li>
 * </ul>
 * <p>
 * The sensor maps {@link HighlightType} values to SonarQube's {@code TypeOfText}
 * enum for saving via the highlighting API.
 */
public final class FeatureHighlighter {

    private FeatureHighlighter() {
        // utility class
    }

    /**
     * Computes highlighting ranges for the given feature file.
     *
     * @param featureFile the parsed feature file tree
     * @return list of highlighting ranges
     */
    public static List<HighlightRange> highlight(FeatureFile featureFile) {
        List<HighlightRange> ranges = new ArrayList<>();

        if (featureFile.feature() != null) {
            highlightFeature(featureFile.feature(), ranges);
        }

        for (Comment comment : featureFile.comments()) {
            highlightComment(comment, ranges);
        }

        return ranges;
    }

    private static void highlightFeature(FeatureDefinition feature, List<HighlightRange> ranges) {
        addKeywordRange(feature.position(), feature.keyword(), ranges);

        for (TagDefinition tag : feature.tags()) {
            addTagRange(tag, ranges);
        }

        if (feature.background() != null) {
            highlightBackground(feature.background(), ranges);
        }

        for (ScenarioDefinition scenario : feature.scenarios()) {
            highlightScenario(scenario, ranges);
        }

        for (RuleDefinition rule : feature.rules()) {
            highlightRule(rule, ranges);
        }
    }

    private static void highlightRule(RuleDefinition rule, List<HighlightRange> ranges) {
        addKeywordRange(rule.position(), rule.keyword(), ranges);

        for (TagDefinition tag : rule.tags()) {
            addTagRange(tag, ranges);
        }

        if (rule.background() != null) {
            highlightBackground(rule.background(), ranges);
        }

        for (ScenarioDefinition scenario : rule.scenarios()) {
            highlightScenario(scenario, ranges);
        }
    }

    private static void highlightScenario(ScenarioDefinition scenario, List<HighlightRange> ranges) {
        addKeywordRange(scenario.position(), scenario.keyword(), ranges);

        for (TagDefinition tag : scenario.tags()) {
            addTagRange(tag, ranges);
        }

        for (StepDefinition step : scenario.steps()) {
            highlightStep(step, ranges);
        }

        for (ExamplesDefinition examples : scenario.examples()) {
            addKeywordRange(examples.position(), examples.keyword(), ranges);
            for (TagDefinition tag : examples.tags()) {
                addTagRange(tag, ranges);
            }
        }
    }

    private static void highlightBackground(BackgroundDefinition background, List<HighlightRange> ranges) {
        addKeywordRange(background.position(), background.keyword(), ranges);

        for (StepDefinition step : background.steps()) {
            highlightStep(step, ranges);
        }
    }

    private static void highlightStep(StepDefinition step, List<HighlightRange> ranges) {
        // Step keywords include trailing space (e.g., "Given "); trim for highlighting
        String keyword = step.keyword().trim();
        if (!keyword.isEmpty()) {
            ranges.add(new HighlightRange(
                    step.position().line(),
                    step.position().column(),
                    step.position().line(),
                    step.position().column() + keyword.length() - 1,
                    HighlightType.KEYWORD));
        }
    }

    private static void addKeywordRange(TextPosition position, String keyword, List<HighlightRange> ranges) {
        // Structural keywords may include trailing colon/space; trim for clean highlighting
        String trimmed = keyword.trim();
        if (!trimmed.isEmpty()) {
            ranges.add(new HighlightRange(
                    position.line(),
                    position.column(),
                    position.line(),
                    position.column() + trimmed.length() - 1,
                    HighlightType.KEYWORD));
        }
    }

    private static void addTagRange(TagDefinition tag, List<HighlightRange> ranges) {
        // Tag name is stored without '@', but in the source it starts with '@'
        // Position points to the '@' character
        int length = tag.name().length() + 1; // +1 for the '@' prefix
        ranges.add(new HighlightRange(
                tag.position().line(),
                tag.position().column(),
                tag.position().line(),
                tag.position().column() + length - 1,
                HighlightType.ANNOTATION));
    }

    private static void highlightComment(Comment comment, List<HighlightRange> ranges) {
        // Comment text from the parser includes leading whitespace and '#'
        // Position points to the '#' character
        // Highlight from '#' to end of the comment text (stripping leading whitespace)
        String text = comment.text();
        if (text != null) {
            String stripped = text.stripLeading();
            if (!stripped.isEmpty()) {
                ranges.add(new HighlightRange(
                        comment.position().line(),
                        comment.position().column(),
                        comment.position().line(),
                        comment.position().column() + stripped.length() - 1,
                        HighlightType.COMMENT));
            }
        }
    }

    /**
     * The type of syntax highlighting to apply.
     */
    public enum HighlightType {
        /** Gherkin keywords (Feature, Scenario, Given, When, Then, etc.) */
        KEYWORD,
        /** Tags (@tag) */
        ANNOTATION,
        /** Comments (# text) */
        COMMENT
    }

    /**
     * Represents a contiguous range in the source file to be highlighted.
     *
     * @param startLine   1-based start line
     * @param startColumn 1-based start column (inclusive)
     * @param endLine     1-based end line
     * @param endColumn   1-based end column (inclusive)
     * @param type        the type of highlighting to apply
     */
    public record HighlightRange(
            int startLine,
            int startColumn,
            int endLine,
            int endColumn,
            HighlightType type) {
    }
}
