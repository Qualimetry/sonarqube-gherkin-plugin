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
package com.qualimetry.sonar.gherkin.analyzer.visitor;

import com.qualimetry.sonar.gherkin.analyzer.parser.model.BackgroundDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.Comment;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;

/**
 * Utility that walks a {@link FeatureFile} tree and invokes the appropriate
 * visit/leave callbacks on a {@link FeatureVisitor}.
 * <p>
 * The traversal order is:
 * <ol>
 *   <li>{@code visitFeatureFile}</li>
 *   <li>If a Feature exists:
 *     <ol>
 *       <li>{@code visitFeature}</li>
 *       <li>Feature-level tags</li>
 *       <li>Feature-level background (+ its steps)</li>
 *       <li>Feature-level scenarios (+ their tags, steps, examples)</li>
 *       <li>Rules (+ their tags, background, scenarios recursively)</li>
 *       <li>{@code leaveFeature}</li>
 *     </ol>
 *   </li>
 *   <li>Comments</li>
 *   <li>{@code leaveFeatureFile}</li>
 * </ol>
 */
public final class FeatureWalker {

    private FeatureWalker() {
        // utility class
    }

    /**
     * Walks the given {@link FeatureFile} tree, calling the visitor methods
     * in the correct order.
     *
     * @param file    the root of the tree to walk
     * @param visitor the visitor to receive callbacks
     */
    public static void walk(FeatureFile file, FeatureVisitor visitor) {
        visitor.visitFeatureFile(file);

        if (file.feature() != null) {
            walkFeature(file.feature(), visitor);
        }

        for (Comment comment : file.comments()) {
            visitor.visitComment(comment);
        }

        visitor.leaveFeatureFile(file);
    }

    private static void walkFeature(FeatureDefinition feature, FeatureVisitor visitor) {
        visitor.visitFeature(feature);

        for (TagDefinition tag : feature.tags()) {
            visitor.visitTag(tag);
        }

        if (feature.background() != null) {
            walkBackground(feature.background(), visitor);
        }

        for (ScenarioDefinition scenario : feature.scenarios()) {
            walkScenario(scenario, visitor);
        }

        for (RuleDefinition rule : feature.rules()) {
            walkRule(rule, visitor);
        }

        visitor.leaveFeature(feature);
    }

    private static void walkRule(RuleDefinition rule, FeatureVisitor visitor) {
        visitor.visitRule(rule);

        for (TagDefinition tag : rule.tags()) {
            visitor.visitTag(tag);
        }

        if (rule.background() != null) {
            walkBackground(rule.background(), visitor);
        }

        for (ScenarioDefinition scenario : rule.scenarios()) {
            walkScenario(scenario, visitor);
        }

        visitor.leaveRule(rule);
    }

    private static void walkScenario(ScenarioDefinition scenario, FeatureVisitor visitor) {
        visitor.visitScenario(scenario);

        for (TagDefinition tag : scenario.tags()) {
            visitor.visitTag(tag);
        }

        for (StepDefinition step : scenario.steps()) {
            visitor.visitStep(step);
        }

        for (ExamplesDefinition examples : scenario.examples()) {
            visitor.visitExamples(examples);
            for (TagDefinition tag : examples.tags()) {
                visitor.visitTag(tag);
            }
        }

        visitor.leaveScenario(scenario);
    }

    private static void walkBackground(BackgroundDefinition background, FeatureVisitor visitor) {
        visitor.visitBackground(background);

        for (StepDefinition step : background.steps()) {
            visitor.visitStep(step);
        }
    }
}
