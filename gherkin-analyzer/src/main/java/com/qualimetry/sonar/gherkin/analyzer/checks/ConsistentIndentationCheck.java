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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.BackgroundDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Checks that Gherkin files use consistent indentation.
 * <p>
 * Uses AST-based visitor callbacks to determine expected indentation for each
 * structural element, making the check language-agnostic and Rule-nesting-aware.
 * <ul>
 *   <li>Feature keyword: level 0</li>
 *   <li>Feature tags: level 0</li>
 *   <li>Rule/Background/Scenario/Scenario Outline at Feature level: level 1</li>
 *   <li>Rule/Scenario tags at Feature level: level 1</li>
 *   <li>Steps at Feature level: level 2</li>
 *   <li>Examples at Feature level: level 2</li>
 *   <li>Examples tags at Feature level: level 2</li>
 *   <li>Data tables at Feature level: level 2</li>
 *   <li>Background/Scenario inside Rule: level 2</li>
 *   <li>Scenario tags inside Rule: level 2</li>
 *   <li>Steps inside Rule: level 3</li>
 *   <li>Examples inside Rule: level 3</li>
 *   <li>Examples tags inside Rule: level 3</li>
 *   <li>Data tables inside Rule: level 3</li>
 * </ul>
 */
@Rule(key = "consistent-indentation")
public class ConsistentIndentationCheck extends BaseCheck {

    private static final int DEFAULT_INDENTATION = 2;

    @RuleProperty(
            key = "indentation",
            description = "Number of spaces per indentation level",
            defaultValue = "" + DEFAULT_INDENTATION)
    private int indentation = DEFAULT_INDENTATION;

    private boolean insideRule;
    private String[] rawLines;

    public void setIndentation(int indentation) {
        this.indentation = indentation;
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        insideRule = false;
        String rawContent = getContext().getRawContent();
        if (rawContent != null) {
            rawLines = rawContent.split("\n", -1);
        } else {
            rawLines = null;
        }
    }

    @Override
    public void visitFeature(FeatureDefinition feature) {
        checkIndentation(feature.position(), 0);
        // Feature-level tags are at level 0
        for (TagDefinition tag : feature.tags()) {
            checkIndentation(tag.position(), 0);
        }
    }

    @Override
    public void visitRule(RuleDefinition rule) {
        checkIndentation(rule.position(), 1);
        // Rule-level tags are at level 1
        for (TagDefinition tag : rule.tags()) {
            checkIndentation(tag.position(), 1);
        }
        insideRule = true;
    }

    @Override
    public void leaveRule(RuleDefinition rule) {
        insideRule = false;
    }

    @Override
    public void visitBackground(BackgroundDefinition background) {
        int level = insideRule ? 2 : 1;
        checkIndentation(background.position(), level);
    }

    @Override
    public void visitScenario(ScenarioDefinition scenario) {
        int level = insideRule ? 2 : 1;
        checkIndentation(scenario.position(), level);
        // Scenario-level tags
        int tagLevel = insideRule ? 2 : 1;
        for (TagDefinition tag : scenario.tags()) {
            checkIndentation(tag.position(), tagLevel);
        }
    }

    @Override
    public void visitStep(StepDefinition step) {
        int level = insideRule ? 3 : 2;
        checkIndentation(step.position(), level);
    }

    @Override
    public void visitExamples(ExamplesDefinition examples) {
        int level = insideRule ? 3 : 2;
        checkIndentation(examples.position(), level);
        // Examples-level tags
        int tagLevel = insideRule ? 3 : 2;
        for (TagDefinition tag : examples.tags()) {
            checkIndentation(tag.position(), tagLevel);
        }
    }

    private void checkIndentation(TextPosition position, int expectedLevel) {
        if (rawLines == null || position == null) {
            return;
        }
        int lineIndex = position.line() - 1;
        if (lineIndex < 0 || lineIndex >= rawLines.length) {
            return;
        }

        String line = rawLines[lineIndex];
        int actualIndent = countLeadingSpaces(line);
        int expectedSpaces = expectedLevel * indentation;

        if (actualIndent != expectedSpaces) {
            addLineIssue(position.line(),
                    String.format("Expected indentation of %d spaces (level %d), but found %d spaces.",
                            expectedSpaces, expectedLevel, actualIndent));
        }
    }

    private static int countLeadingSpaces(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
