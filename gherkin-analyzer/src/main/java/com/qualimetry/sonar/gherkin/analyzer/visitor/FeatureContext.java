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

import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import org.sonar.api.batch.fs.InputFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Holds the context for a single file analysis pass.
 * <p>
 * Contains the parsed {@link FeatureFile}, the optional SonarQube
 * {@link InputFile}, the raw file content as a string (for checks that
 * need raw text access), and a mutable list of collected {@link Issue}s.
 * <p>
 * The {@code inputFile} is nullable: it is {@code null} when running
 * checks in test mode (e.g., via CheckVerifier) where no SonarQube
 * environment is available. The {@code rawContent} is also nullable
 * but should be provided when raw-content checks (indentation, tabs,
 * trailing whitespace, etc.) are executed.
 */
public class FeatureContext {

    private final FeatureFile featureFile;
    private final InputFile inputFile;
    private final String rawContent;
    private final List<Issue> issues;

    /**
     * Creates a context with all fields.
     *
     * @param featureFile the parsed feature file (required)
     * @param inputFile   the SonarQube input file, or {@code null} in test mode
     * @param rawContent  the raw file content as a string, or {@code null} if not available
     */
    public FeatureContext(FeatureFile featureFile, InputFile inputFile, String rawContent) {
        this.featureFile = Objects.requireNonNull(featureFile, "featureFile must not be null");
        this.inputFile = inputFile;
        this.rawContent = rawContent;
        this.issues = new ArrayList<>();
    }

    /**
     * Convenience constructor without raw content.
     *
     * @param featureFile the parsed feature file (required)
     * @param inputFile   the SonarQube input file, or {@code null} in test mode
     */
    public FeatureContext(FeatureFile featureFile, InputFile inputFile) {
        this(featureFile, inputFile, null);
    }

    /**
     * Convenience constructor for test mode (no InputFile, no raw content).
     *
     * @param featureFile the parsed feature file (required)
     */
    public FeatureContext(FeatureFile featureFile) {
        this(featureFile, null, null);
    }

    public FeatureFile getFeatureFile() {
        return featureFile;
    }

    /**
     * Returns the SonarQube input file, or {@code null} if running in test mode.
     */
    public InputFile getInputFile() {
        return inputFile;
    }

    /**
     * Returns the raw file content as a string, or {@code null} if not provided.
     */
    public String getRawContent() {
        return rawContent;
    }

    /**
     * Adds an issue to the collected issues list.
     *
     * @param issue the issue to add
     */
    public void addIssue(Issue issue) {
        Objects.requireNonNull(issue, "issue must not be null");
        issues.add(issue);
    }

    /**
     * Returns an unmodifiable view of the collected issues.
     */
    public List<Issue> getIssues() {
        return Collections.unmodifiableList(issues);
    }
}
