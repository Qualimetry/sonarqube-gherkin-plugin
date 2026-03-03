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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks that every {@code Feature} has a non-empty name.
 * <p>
 * A meaningful Feature name is essential for understanding the purpose of
 * a test suite at a glance. Unnamed features make reports unreadable and
 * hinder team collaboration.
 */
@Rule(key = "feature-name-required")
public class FeatureNameRequiredCheck extends BaseCheck {

    @Override
    public void visitFeature(FeatureDefinition feature) {
        if (feature.name() == null || feature.name().isBlank()) {
            addIssue(feature.position(), "Add a name to this Feature.");
        }
    }
}
