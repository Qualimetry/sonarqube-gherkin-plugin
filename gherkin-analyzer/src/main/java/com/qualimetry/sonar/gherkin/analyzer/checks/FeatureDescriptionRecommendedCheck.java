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
 * Checks that every {@code Feature} includes a description.
 * <p>
 * A description below the Feature name provides valuable context about the
 * business domain and the purpose of the scenarios in the file. Without it,
 * stakeholders may not understand the intent of the test suite.
 */
@Rule(key = "feature-description-recommended")
public class FeatureDescriptionRecommendedCheck extends BaseCheck {

    @Override
    public void visitFeature(FeatureDefinition feature) {
        if (feature.description() == null || feature.description().isBlank()) {
            addIssue(feature.position(), "Add a description to this Feature.");
        }
    }
}
