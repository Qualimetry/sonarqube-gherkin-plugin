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
package com.qualimetry.sonar.gherkin.plugin;

import com.qualimetry.sonar.gherkin.analyzer.checks.CheckList;
import org.junit.jupiter.api.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInQualityProfile;

import static org.assertj.core.api.Assertions.assertThat;

class GherkinQualityProfileTest {

    @Test
    void shouldCreateProfile() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        new GherkinQualityProfile().define(context);

        BuiltInQualityProfile profile = context.profile("gherkin", "Qualimetry Gherkin");
        assertThat(profile).isNotNull();
    }

    @Test
    void shouldBeDefaultProfile() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        new GherkinQualityProfile().define(context);

        BuiltInQualityProfile profile = context.profile("gherkin", "Qualimetry Gherkin");
        assertThat(profile.isDefault()).isTrue();
    }

    @Test
    void shouldActivate50Rules() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        new GherkinQualityProfile().define(context);

        BuiltInQualityProfile profile = context.profile("gherkin", "Qualimetry Gherkin");
        assertThat(profile.rules()).hasSize(53);
    }

    @Test
    void shouldActivateRulesFromCorrectRepository() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        new GherkinQualityProfile().define(context);

        BuiltInQualityProfile profile = context.profile("gherkin", "Qualimetry Gherkin");
        assertThat(profile.rules()).allSatisfy(rule ->
                assertThat(rule.repoKey()).isEqualTo(CheckList.REPOSITORY_KEY));
    }

    @Test
    void shouldActivateFeatureFileRequiredRule() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        new GherkinQualityProfile().define(context);

        BuiltInQualityProfile profile = context.profile("gherkin", "Qualimetry Gherkin");
        assertThat(profile.rules()).anySatisfy(rule ->
                assertThat(rule.ruleKey()).isEqualTo("feature-file-required"));
    }

    @Test
    void shouldNotActivateNonDefaultRules() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        new GherkinQualityProfile().define(context);

        BuiltInQualityProfile profile = context.profile("gherkin", "Qualimetry Gherkin");
        // These rules are not in the default profile
        assertThat(profile.rules()).noneSatisfy(rule ->
                assertThat(rule.ruleKey()).isEqualTo("feature-description-recommended"));
        assertThat(profile.rules()).noneSatisfy(rule ->
                assertThat(rule.ruleKey()).isEqualTo("consistent-feature-language"));
        assertThat(profile.rules()).noneSatisfy(rule ->
                assertThat(rule.ruleKey()).isEqualTo("spelling-accuracy"));
        // no-star-step-prefix not in default profile (spec-endorsed syntax)
        assertThat(profile.rules()).noneSatisfy(rule ->
                assertThat(rule.ruleKey()).isEqualTo("no-star-step-prefix"));
    }
}
