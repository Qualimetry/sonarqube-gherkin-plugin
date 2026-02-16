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

import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Marker rule used by the sensor to report parse errors.
 * <p>
 * When the Gherkin parser fails to parse a feature file, the sensor
 * creates an issue against this rule key at the location of the parse
 * error. The check class itself is a no-op &mdash; it exists only to
 * register the rule key with the SonarQube rule repository.
 */
@Rule(key = "parse-error")
public class ParseErrorCheck extends BaseCheck {

    // No-op: parse errors are reported directly by the sensor.
}
