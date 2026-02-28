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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;

/**
 * Checks that feature file names follow a naming convention pattern.
 * <p>
 * By default, requires filenames to start with a lowercase letter and
 * contain only letters, numbers, hyphens, and underscores, ending with
 * the {@code .feature} extension.
 */
@Rule(key = "file-name-convention")
public class FileNameConventionCheck extends BaseCheck {

    private static final String DEFAULT_PATTERN = "^[a-z][-A-Za-z0-9]*\\.feature$";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression pattern that feature filenames must match",
            defaultValue = DEFAULT_PATTERN)
    private String pattern = DEFAULT_PATTERN;

    private Pattern compiledPattern;

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = null; // Reset compiled pattern
    }

    @Override
    public void visitFeatureFile(FeatureFile file) {
        if (compiledPattern == null) {
            compiledPattern = Pattern.compile(pattern);
        }

        String uri = file.uri();
        String filename = extractFilename(uri);

        if (!compiledPattern.matcher(filename).matches()) {
            addLineIssue(1, "Filename '" + filename + "' does not match the required pattern: " + pattern);
        }
    }

    private String extractFilename(String uri) {
        // Handle both forward and backslash path separators
        String normalized = uri.replace('\\', '/');
        int lastSlash = normalized.lastIndexOf('/');
        if (lastSlash >= 0) {
            return normalized.substring(lastSlash + 1);
        }
        return normalized;
    }
}
