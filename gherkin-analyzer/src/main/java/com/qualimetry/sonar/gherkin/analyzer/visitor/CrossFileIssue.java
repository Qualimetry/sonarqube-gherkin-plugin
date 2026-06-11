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
package com.qualimetry.sonar.gherkin.analyzer.visitor;

/**
 * Represents an issue found by a cross-file check that needs to be
 * routed to a specific file identified by URI.
 * <p>
 * Unlike regular {@link Issue}s which are attached to the current file's
 * {@link FeatureContext}, cross-file issues specify their target file
 * via URI because the issue may belong to a file other than the one
 * whose context was last set on the check.
 *
 * @param ruleKey the key of the rule that raised this issue
 * @param uri     the URI of the file this issue belongs to
 * @param line    the 1-based line number within the target file
 * @param message the human-readable issue message
 */
public record CrossFileIssue(
        String ruleKey,
        String uri,
        int line,
        String message) {
}
