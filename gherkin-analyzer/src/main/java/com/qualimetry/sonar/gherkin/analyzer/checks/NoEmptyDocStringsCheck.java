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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that doc strings attached to steps are not empty.
 * <p>
 * An empty doc string serves no purpose - it was either left as a
 * placeholder that was never filled in, or its content was deleted
 * without removing the delimiters. This is always a mistake.
 */
@Rule(key = "no-empty-doc-strings")
public class NoEmptyDocStringsCheck extends BaseCheck {

    @Override
    public void visitStep(StepDefinition step) {
        if (step.docString() != null && step.docString().content().isBlank()) {
            addIssue(step.position(), "Remove or fill in this empty doc string.");
        }
    }
}
