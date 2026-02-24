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
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

/**
 * Defines the "Qualimetry Gherkin" built-in quality profile as the default
 * for the Gherkin language. Activates the rules included in the default profile.
 */
public class GherkinQualityProfile implements BuiltInQualityProfilesDefinition {

    /** The display name of the default quality profile. */
    static final String PROFILE_NAME = "Qualimetry Gherkin";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(
                PROFILE_NAME, GherkinLanguage.KEY);
        profile.setDefault(true);

        for (String ruleKey : CheckList.getDefaultRuleKeys()) {
            profile.activateRule(CheckList.REPOSITORY_KEY, ruleKey);
        }

        profile.done();
    }
}
