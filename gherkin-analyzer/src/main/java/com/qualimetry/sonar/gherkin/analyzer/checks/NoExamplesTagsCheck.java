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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that tags are not placed on Examples sections.
 * <p>
 * Tags should be placed on Scenario Outlines, not on their Examples sections.
 * Tags on Examples sections are not meaningful and should be moved up to the
 * Scenario Outline level.
 */
@Rule(key = "no-examples-tags")
public class NoExamplesTagsCheck extends BaseCheck {

    private boolean inExamples = false;

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        inExamples = true;
    }

    @Override
    public void visitTag(TagDefinition tag) {
        if (inExamples) {
            addIssue(tag.position(), "Move this tag up to the Scenario Outline level. Tags should not be placed on Examples sections.");
        }
    }

    @Override
    public void visitScenario(com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition scenario) {
        // Reset flag when we enter a new scenario
        // (examples tags are visited after visitExamples, so this ensures clean state)
        inExamples = false;
    }

    @Override
    public void leaveScenario(com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition scenario) {
        // Reset flag when leaving scenario to ensure clean state for next scenario
        inExamples = false;
    }
}
