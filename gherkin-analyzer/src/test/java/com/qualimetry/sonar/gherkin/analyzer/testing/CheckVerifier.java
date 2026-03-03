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
package com.qualimetry.sonar.gherkin.analyzer.testing;

import com.qualimetry.sonar.gherkin.analyzer.parser.FeatureParser;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureContext;
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureWalker;
import com.qualimetry.sonar.gherkin.analyzer.visitor.Issue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Test harness for verifying Gherkin analysis checks against annotated
 * {@code .feature} fixture files.
 * <p>
 * Fixture files use {@code # Noncompliant} annotations to mark expected issues.
 * The annotation is placed on the line <strong>before</strong> the line where
 * the issue is expected (i.e., {@code # Noncompliant} on line N means the
 * expected issue is on line N+1).
 * <p>
 * Supported annotation syntax:
 * <ul>
 *   <li>{@code # Noncompliant} - expects an issue on the next line (line number only)</li>
 *   <li>{@code # Noncompliant {{Expected message}}} - also asserts the issue message</li>
 *   <li>{@code # Noncompliant [[sc=N;ec=N]] {{Expected message}}} - also asserts column positions</li>
 * </ul>
 * <p>
 * The verifier reads the fixture file into a {@code String} first, then passes
 * the same content both to {@link FeatureParser} (via {@link ByteArrayInputStream})
 * and as {@code rawContent} to {@link FeatureContext}. This ensures raw-content
 * checks have access to the original text.
 */
public final class CheckVerifier {

    /**
     * Pattern to detect {@code # Noncompliant} annotations in fixture file lines.
     * <p>
     * Groups:
     * <ol>
     *   <li>Optional {@code :file} modifier (for file-level issues at line 0)</li>
     *   <li>Optional column spec: contents of {@code [[...]]}</li>
     *   <li>Optional message: contents of {@code {{...}}}</li>
     * </ol>
     */
    private static final Pattern NONCOMPLIANT_PATTERN = Pattern.compile(
            "#\\s*Noncompliant(:file)?"
                    + "(?:\\s*\\[\\[([^\\]]+)\\]\\])?"
                    + "(?:\\s*\\{\\{([^}]+)\\}\\})?");

    /** Extracts {@code sc=N} from a column spec string. */
    private static final Pattern SC_PATTERN = Pattern.compile("sc=(\\d+)");

    /** Extracts {@code ec=N} from a column spec string. */
    private static final Pattern EC_PATTERN = Pattern.compile("ec=(\\d+)");

    private CheckVerifier() {
        // utility class
    }

    /**
     * Parses the fixture file, runs the check, and asserts that actual issues
     * match the {@code # Noncompliant} annotations in the file.
     * <p>
     * A {@code # Noncompliant} annotation on line N signals an expected issue
     * on line N+1 (the next line). If a {@code {{message}}} is provided, the
     * actual issue message must match exactly. If {@code [[sc=N;ec=N]]} is
     * provided, the actual issue start/end columns must also match.
     *
     * @param check       the check to run (a fresh instance is recommended)
     * @param fixturePath the classpath resource path to the fixture file
     *                    (e.g., {@code "checks/feature-file-required/noncompliant.feature"})
     * @throws AssertionError if actual issues do not match expected annotations
     */
    public static void verify(BaseCheck check, String fixturePath) {
        String rawContent = readFixture(fixturePath);
        FeatureFile featureFile = parseContent(rawContent, fixturePath);

        // Extract expected issues from # Noncompliant annotations in raw text
        List<TestIssue> expected = extractExpectedIssues(rawContent);

        // Run the check
        FeatureContext context = new FeatureContext(featureFile, null, rawContent);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        // Convert actual issues to TestIssue for comparison
        List<TestIssue> actual = context.getIssues().stream()
                .map(CheckVerifier::toTestIssue)
                .sorted(Comparator.comparingInt(TestIssue::line))
                .toList();

        // Compare expected vs actual
        compareIssues(expected, actual, fixturePath);
    }

    /**
     * Verifies that running the check on the fixture produces zero issues.
     * Use for compliant fixture files that should not trigger the check.
     *
     * @param check       the check to run
     * @param fixturePath the classpath resource path to the fixture file
     * @throws AssertionError if any issues are raised
     */
    public static void verifyNoIssues(BaseCheck check, String fixturePath) {
        String rawContent = readFixture(fixturePath);
        FeatureFile featureFile = parseContent(rawContent, fixturePath);

        FeatureContext context = new FeatureContext(featureFile, null, rawContent);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        List<Issue> issues = context.getIssues();
        if (!issues.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected no issues but found ").append(issues.size());
            sb.append(" in ").append(fixturePath).append(":\n");
            for (Issue issue : issues) {
                sb.append("  Line ").append(issue.line() != null ? issue.line() : "N/A");
                sb.append(": ").append(issue.message()).append("\n");
            }
            throw new AssertionError(sb.toString());
        }
    }

    // ------------------------------------------------------------------
    // Evidence collection (non-throwing)
    // ------------------------------------------------------------------

    /**
     * Parses the fixture file, runs the check, and returns a structured
     * {@link FixtureResult} instead of throwing on mismatch. This is the
     * evidence-collection counterpart of {@link #verify(BaseCheck, String)}.
     *
     * @param check       the check to run (a fresh instance is recommended)
     * @param fixturePath the classpath resource path to the fixture file
     * @return a {@link FixtureResult} containing expected issues, actual issues,
     *         pass/fail status, and any mismatch descriptions
     */
    public static FixtureResult collectEvidence(BaseCheck check, String fixturePath) {
        String rawContent = readFixture(fixturePath);
        FeatureFile featureFile = parseContent(rawContent, fixturePath);
        List<TestIssue> expected = extractExpectedIssues(rawContent);

        FeatureContext context = new FeatureContext(featureFile, null, rawContent);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);

        List<TestIssue> actual = context.getIssues().stream()
                .map(CheckVerifier::toTestIssue)
                .sorted(Comparator.comparingInt(TestIssue::line))
                .toList();

        List<String> mismatches = buildMismatches(expected, actual);
        String status = mismatches.isEmpty()
                ? FixtureResult.STATUS_PASS : FixtureResult.STATUS_FAIL;
        return new FixtureResult(fixturePath, rawContent, expected, actual,
                status, mismatches);
    }

    /**
     * Builds a {@link FixtureResult} from an already-walked {@link FeatureContext}.
     * Use this for cross-file checks where a single check instance is walked
     * over multiple fixtures before issues are finalized (via {@code afterAllFiles()}).
     *
     * @param context     the context that was walked (containing actual issues)
     * @param fixturePath the classpath resource path of the fixture file
     * @param rawContent  the raw text content of the fixture file
     * @return a {@link FixtureResult} with expected-vs-actual comparison
     */
    public static FixtureResult collectEvidenceFromContext(
            FeatureContext context, String fixturePath, String rawContent) {
        List<TestIssue> expected = extractExpectedIssues(rawContent);
        List<TestIssue> actual = context.getIssues().stream()
                .map(CheckVerifier::toTestIssue)
                .sorted(Comparator.comparingInt(TestIssue::line))
                .toList();

        List<String> mismatches = buildMismatches(expected, actual);
        String status = mismatches.isEmpty()
                ? FixtureResult.STATUS_PASS : FixtureResult.STATUS_FAIL;
        return new FixtureResult(fixturePath, rawContent, expected, actual,
                status, mismatches);
    }

    /**
     * Reads a fixture file from the classpath and returns its raw content.
     * Public accessor for use by evidence report infrastructure.
     *
     * @param fixturePath the classpath resource path
     * @return the file content as a string
     */
    public static String readFixtureContent(String fixturePath) {
        return readFixture(fixturePath);
    }

    /**
     * Parses a fixture file and walks it with the given check, returning
     * the {@link FeatureContext} for later evidence collection. Use this
     * with {@link #collectEvidenceFromContext} for cross-file checks.
     *
     * @param check       the check instance (may be shared across files)
     * @param fixturePath the classpath resource path to the fixture file
     * @return the walked context containing actual issues for this file
     */
    public static FeatureContext walkFixture(BaseCheck check, String fixturePath) {
        String rawContent = readFixture(fixturePath);
        FeatureFile featureFile = parseContent(rawContent, fixturePath);
        FeatureContext context = new FeatureContext(featureFile, null, rawContent);
        check.setContext(context);
        FeatureWalker.walk(featureFile, check);
        return context;
    }

    // ------------------------------------------------------------------
    // Fixture reading and parsing
    // ------------------------------------------------------------------

    /**
     * Reads a fixture file from the classpath into a string.
     *
     * @param fixturePath the classpath resource path
     * @return the file content as a string
     * @throws IllegalArgumentException if the fixture is not found
     */
    private static String readFixture(String fixturePath) {
        String resourcePath = fixturePath.startsWith("/") ? fixturePath : "/" + fixturePath;
        try (InputStream input = CheckVerifier.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalArgumentException(
                        "Fixture file not found on classpath: " + fixturePath
                                + " (resolved to resource path: " + resourcePath + ")");
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read fixture file: " + fixturePath, e);
        }
    }

    /**
     * Parses raw content into a {@link FeatureFile} using {@link FeatureParser}.
     * The fixture path is used as the URI for the parsed file (per Development
     * Note 30, this enables checks that inspect the file name).
     *
     * @param rawContent  the raw file content
     * @param fixturePath the fixture path, used as the parser URI
     * @return the parsed feature file
     */
    private static FeatureFile parseContent(String rawContent, String fixturePath) {
        FeatureParser parser = new FeatureParser();
        try {
            return parser.parse(
                    fixturePath,
                    new ByteArrayInputStream(rawContent.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse fixture: " + fixturePath, e);
        }
    }

    // ------------------------------------------------------------------
    // Annotation extraction
    // ------------------------------------------------------------------

    /**
     * Scans the raw file content line by line and extracts expected issues
     * from {@code # Noncompliant} annotations.
     * <p>
     * A {@code # Noncompliant} annotation on line N (1-based) produces an
     * expected issue on line N+1 (the next line).
     *
     * @param rawContent the raw file content
     * @return a list of expected issues sorted by line number
     */
    static List<TestIssue> extractExpectedIssues(String rawContent) {
        List<TestIssue> issues = new ArrayList<>();
        String[] lines = rawContent.split("\\r?\\n", -1);

        for (int i = 0; i < lines.length; i++) {
            Matcher matcher = NONCOMPLIANT_PATTERN.matcher(lines[i]);
            if (matcher.find()) {
                // Group 1: optional ":file" modifier
                // Group 2: optional column spec [[...]]
                // Group 3: optional message {{...}}
                boolean fileLevel = matcher.group(1) != null;
                int issueLine = fileLevel
                        ? 0                     // file-level issue (no specific line)
                        : i + 2;                // annotation on line i → issue on next line

                String columnSpec = matcher.group(2);
                String message = matcher.group(3);

                Integer startColumn = null;
                Integer endColumn = null;

                if (columnSpec != null) {
                    Matcher scMatcher = SC_PATTERN.matcher(columnSpec);
                    if (scMatcher.find()) {
                        startColumn = Integer.parseInt(scMatcher.group(1));
                    }
                    Matcher ecMatcher = EC_PATTERN.matcher(columnSpec);
                    if (ecMatcher.find()) {
                        endColumn = Integer.parseInt(ecMatcher.group(1));
                    }
                }

                issues.add(new TestIssue(issueLine, message, startColumn, endColumn));
            }
        }

        issues.sort(Comparator.comparingInt(TestIssue::line));
        return issues;
    }

    // ------------------------------------------------------------------
    // Issue comparison
    // ------------------------------------------------------------------

    /**
     * Converts an internal {@link Issue} to a {@link TestIssue} for comparison.
     * File-level issues (with null line) are mapped to line 0.
     */
    private static TestIssue toTestIssue(Issue issue) {
        int line = issue.line() != null ? issue.line() : 0;
        Integer startColumn = issue.position() != null ? issue.position().column() : null;
        return new TestIssue(line, issue.message(), startColumn, null);
    }

    /**
     * Compares expected and actual issue lists and throws an {@link AssertionError}
     * with a detailed report if they do not match.
     */
    private static void compareIssues(List<TestIssue> expected, List<TestIssue> actual,
                                      String fixturePath) {
        List<String> errors = new ArrayList<>();

        // Group issues by line number
        Map<Integer, List<TestIssue>> expectedByLine = groupByLine(expected);
        Map<Integer, List<TestIssue>> actualByLine = groupByLine(actual);

        // Collect all line numbers from both sides
        Set<Integer> allLines = new TreeSet<>();
        allLines.addAll(expectedByLine.keySet());
        allLines.addAll(actualByLine.keySet());

        for (int line : allLines) {
            List<TestIssue> expAtLine = expectedByLine.getOrDefault(line, List.of());
            List<TestIssue> actAtLine = actualByLine.getOrDefault(line, List.of());

            if (!expAtLine.isEmpty() && actAtLine.isEmpty()) {
                // Expected issues but none found
                errors.add("  Line " + line + ": expected " + expAtLine.size()
                        + " issue(s) but found none.");
            } else if (expAtLine.isEmpty() && !actAtLine.isEmpty()) {
                // Unexpected issues found
                for (TestIssue issue : actAtLine) {
                    errors.add("  Line " + line + ": unexpected issue: " + issue.message());
                }
            } else {
                // Both sides have issues at this line - compare counts and details
                if (expAtLine.size() != actAtLine.size()) {
                    errors.add("  Line " + line + ": expected " + expAtLine.size()
                            + " issue(s) but found " + actAtLine.size() + ".");
                }
                int checkCount = Math.min(expAtLine.size(), actAtLine.size());
                for (int i = 0; i < checkCount; i++) {
                    compareIssueDetails(expAtLine.get(i), actAtLine.get(i), errors);
                }
            }
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Issue verification failed for ").append(fixturePath).append(":\n");
            for (String error : errors) {
                sb.append(error).append("\n");
            }
            throw new AssertionError(sb.toString());
        }
    }

    /**
     * Compares individual issue details (message, columns) and adds any
     * mismatches to the error list.
     */
    private static void compareIssueDetails(TestIssue expected, TestIssue actual,
                                            List<String> errors) {
        int line = expected.line();

        if (expected.message() != null && !expected.message().equals(actual.message())) {
            errors.add("  Line " + line + ": expected message \""
                    + expected.message() + "\" but got \"" + actual.message() + "\".");
        }

        if (expected.startColumn() != null
                && !expected.startColumn().equals(actual.startColumn())) {
            errors.add("  Line " + line + ": expected startColumn "
                    + expected.startColumn() + " but got " + actual.startColumn() + ".");
        }

        if (expected.endColumn() != null
                && !expected.endColumn().equals(actual.endColumn())) {
            errors.add("  Line " + line + ": expected endColumn "
                    + expected.endColumn() + " but got " + actual.endColumn() + ".");
        }
    }

    /**
     * Groups a list of {@link TestIssue}s by line number, preserving order
     * within each line group.
     */
    private static Map<Integer, List<TestIssue>> groupByLine(List<TestIssue> issues) {
        return issues.stream()
                .collect(Collectors.groupingBy(
                        TestIssue::line,
                        TreeMap::new,
                        Collectors.toList()));
    }

    // ------------------------------------------------------------------
    // Non-throwing mismatch detection (for evidence collection)
    // ------------------------------------------------------------------

    /**
     * Compares expected and actual issue lists and returns human-readable
     * mismatch descriptions. Returns an empty list if they match exactly.
     * This is the non-throwing counterpart of {@link #compareIssues}.
     */
    private static List<String> buildMismatches(List<TestIssue> expected,
                                                List<TestIssue> actual) {
        List<String> errors = new ArrayList<>();

        Map<Integer, List<TestIssue>> expectedByLine = groupByLine(expected);
        Map<Integer, List<TestIssue>> actualByLine = groupByLine(actual);

        Set<Integer> allLines = new TreeSet<>();
        allLines.addAll(expectedByLine.keySet());
        allLines.addAll(actualByLine.keySet());

        for (int line : allLines) {
            List<TestIssue> expAtLine = expectedByLine.getOrDefault(line, List.of());
            List<TestIssue> actAtLine = actualByLine.getOrDefault(line, List.of());

            if (!expAtLine.isEmpty() && actAtLine.isEmpty()) {
                errors.add("Line " + line + ": expected " + expAtLine.size()
                        + " issue(s) but found none.");
            } else if (expAtLine.isEmpty() && !actAtLine.isEmpty()) {
                for (TestIssue issue : actAtLine) {
                    errors.add("Line " + line + ": unexpected issue: " + issue.message());
                }
            } else {
                if (expAtLine.size() != actAtLine.size()) {
                    errors.add("Line " + line + ": expected " + expAtLine.size()
                            + " issue(s) but found " + actAtLine.size() + ".");
                }
                int checkCount = Math.min(expAtLine.size(), actAtLine.size());
                for (int i = 0; i < checkCount; i++) {
                    buildIssueDetailMismatches(expAtLine.get(i), actAtLine.get(i), errors);
                }
            }
        }

        return errors;
    }

    /**
     * Compares individual issue details and appends mismatch descriptions.
     */
    private static void buildIssueDetailMismatches(TestIssue expected, TestIssue actual,
                                                   List<String> errors) {
        int line = expected.line();

        if (expected.message() != null && !expected.message().equals(actual.message())) {
            errors.add("Line " + line + ": expected message \""
                    + expected.message() + "\" but got \"" + actual.message() + "\".");
        }

        if (expected.startColumn() != null
                && !expected.startColumn().equals(actual.startColumn())) {
            errors.add("Line " + line + ": expected startColumn "
                    + expected.startColumn() + " but got " + actual.startColumn() + ".");
        }

        if (expected.endColumn() != null
                && !expected.endColumn().equals(actual.endColumn())) {
            errors.add("Line " + line + ": expected endColumn "
                    + expected.endColumn() + " but got " + actual.endColumn() + ".");
        }
    }
}
