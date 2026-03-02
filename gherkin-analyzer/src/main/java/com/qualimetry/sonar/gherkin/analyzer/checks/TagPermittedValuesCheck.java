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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;

/**
 * Checks that tag names match a permitted pattern.
 * <p>
 * By default, allows any tag name (pattern {@code .*}). Configure the pattern
 * to restrict tags to a specific set of values or naming conventions.
 */
@Rule(key = "tag-permitted-values")
public class TagPermittedValuesCheck extends BaseCheck {

    private static final String DEFAULT_PATTERN = ".*";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression pattern that tag names must match",
            defaultValue = DEFAULT_PATTERN)
    private String pattern = DEFAULT_PATTERN;

    private Pattern compiledPattern;

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = null; // Reset compiled pattern
    }

    @Override
    public void visitTag(TagDefinition tag) {
        if (compiledPattern == null) {
            compiledPattern = Pattern.compile(pattern);
        }

        String tagName = tag.name();
        if (!compiledPattern.matcher(tagName).matches()) {
            addIssue(tag.position(), "Tag name '" + tagName + "' does not match the permitted pattern: " + pattern);
        }
    }
}
