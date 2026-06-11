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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Flags tags that match a configurable restricted list.
 * <p>
 * Tags such as {@code @wip}, {@code @debug}, {@code @ignore}, or {@code @manual}
 * are often used during development but should not remain in committed feature files.
 * This check reports any tag whose name (without the leading {@code @}) appears in
 * the configurable restricted list.
 */
@Rule(key = "no-restricted-tags")
public class NoRestrictedTagsCheck extends BaseCheck {

    private static final String DEFAULT_RESTRICTED_TAGS = "wip,debug,ignore,manual";

    @RuleProperty(
            key = "restrictedTags",
            description = "Comma-separated list of restricted tag names (without leading @)",
            defaultValue = DEFAULT_RESTRICTED_TAGS)
    private String restrictedTags = DEFAULT_RESTRICTED_TAGS;

    private Set<String> restrictedSet;

    public void setRestrictedTags(String restrictedTags) {
        this.restrictedTags = restrictedTags;
        this.restrictedSet = null; // reset cached set
    }

    private Set<String> getRestrictedSet() {
        if (restrictedSet == null) {
            restrictedSet = Arrays.stream(restrictedTags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
        }
        return restrictedSet;
    }

    @Override
    public void visitTag(TagDefinition tag) {
        if (getRestrictedSet().contains(tag.name())) {
            addIssue(tag.position(),
                    "Remove the restricted tag \"@" + tag.name() + "\"; it should not be committed.");
        }
    }
}
