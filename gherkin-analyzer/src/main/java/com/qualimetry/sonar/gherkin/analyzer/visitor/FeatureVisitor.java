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
 * Visitor interface for traversing the Gherkin tree model.
 * <p>
 * Each {@code visit*} method is called when entering a node and each
 * {@code leave*} method is called when leaving it. Default implementations
 * are no-ops so that concrete visitors can override only the methods they
 * care about.
 */
public interface FeatureVisitor {

    default void visitFeatureFile(FeatureFile file) {
    }

    default void visitFeature(FeatureDefinition feature) {
    }

    default void visitBackground(BackgroundDefinition background) {
    }

    default void visitScenario(ScenarioDefinition scenario) {
    }

    default void visitRule(RuleDefinition rule) {
    }

    default void visitStep(StepDefinition step) {
    }

    default void visitTag(TagDefinition tag) {
    }

    default void visitExamples(ExamplesDefinition examples) {
    }

    default void visitComment(Comment comment) {
    }

    default void leaveFeatureFile(FeatureFile file) {
    }

    default void leaveFeature(FeatureDefinition feature) {
    }

    default void leaveScenario(ScenarioDefinition scenario) {
    }

    default void leaveRule(RuleDefinition rule) {
    }
}
