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

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Checks that steps and names use business language rather than
 * technical UI terms.
 * <p>
 * Gherkin scenarios should describe business behaviour, not implementation
 * details. References to UI elements like buttons, links, or pages
 * couple the tests to the implementation and make them brittle.
 */
@Rule(key = "business-language-only")
public class BusinessLanguageOnlyCheck extends BaseCheck {

    // Technical UI/implementation terms; excludes ordinary user-facing words like "page" and "button"
    private static final Set<String> FORBIDDEN_TERMS = Set.of(
            "click", "checkbox", "dropdown", "field", "link",
            "screen", "select", "submit", "textbox", "url",
            "radio", "input", "textarea", "element", "div", "span",
            "modal", "popup", "tab", "menu", "toolbar", "icon",
            "hover", "scroll", "drag", "css", "html", "xpath"
    );

    private static final Pattern WORD_BOUNDARY = Pattern.compile("\\b");

    @Override
    public void visitStep(StepDefinition step) {
        String text = step.text().toLowerCase();
        String[] words = WORD_BOUNDARY.split(text);
        for (String word : words) {
            String trimmed = word.trim();
            if (FORBIDDEN_TERMS.contains(trimmed)) {
                addIssue(step.position(),
                        "Replace the technical term \"" + trimmed
                                + "\" with business-level language.");
                return; // report only the first forbidden term per step
            }
        }
    }
}
