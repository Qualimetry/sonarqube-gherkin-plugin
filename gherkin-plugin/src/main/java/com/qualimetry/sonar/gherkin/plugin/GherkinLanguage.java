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

import org.sonar.api.resources.AbstractLanguage;

/**
 * Registers the Gherkin language with SonarQube so that {@code .feature}
 * files are recognized and processed by the analysis engine.
 */
public class GherkinLanguage extends AbstractLanguage {

    /** The language key used in SonarQube configuration and API calls. */
    public static final String KEY = "gherkin";

    /** The human-readable language name displayed in the SonarQube UI. */
    public static final String NAME = "Gherkin";

    /** The file suffix (without leading dot) for Gherkin feature files. */
    public static final String FILE_SUFFIX = "feature";

    public GherkinLanguage() {
        super(KEY, NAME);
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[]{FILE_SUFFIX};
    }
}
