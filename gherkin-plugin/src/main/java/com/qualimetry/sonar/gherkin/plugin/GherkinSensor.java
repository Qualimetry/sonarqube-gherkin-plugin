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
import com.qualimetry.sonar.gherkin.analyzer.checks.ConsistentFeatureLanguageCheck;
import com.qualimetry.sonar.gherkin.analyzer.checks.UniqueFeatureNameCheck;
import com.qualimetry.sonar.gherkin.analyzer.checks.UniqueScenarioNameCheck;
import com.qualimetry.sonar.gherkin.analyzer.highlighting.FeatureHighlighter;
import com.qualimetry.sonar.gherkin.analyzer.metrics.FeatureMetrics;
import com.qualimetry.sonar.gherkin.analyzer.parser.FeatureParser;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import com.qualimetry.sonar.gherkin.analyzer.visitor.CrossFileIssue;
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureContext;
import com.qualimetry.sonar.gherkin.analyzer.visitor.FeatureWalker;
import com.qualimetry.sonar.gherkin.analyzer.visitor.Issue;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.check.Rule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main sensor that processes {@code .feature} files for Gherkin analysis.
 * <p>
 * For each file, the sensor:
 * <ol>
 *   <li>Reads the file content once (Note 72)</li>
 *   <li>Parses it with {@link FeatureParser}</li>
 *   <li>Runs all active checks via {@link FeatureWalker}</li>
 *   <li>Saves issues to the {@link SensorContext}</li>
 *   <li>Computes and saves metrics</li>
 *   <li>Applies syntax highlighting</li>
 * </ol>
 * <p>
 * Cross-file checks (Rules 16, 17, 21) accumulate state across files
 * and produce structured {@link CrossFileIssue} results after all files
 * are processed (Note 74, option a).
 * <p>
 * Parse failures are handled per Note 78: Gherkin syntax errors produce
 * a {@code parse-error} rule issue; I/O errors are logged as sensor errors.
 */
public class GherkinSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(GherkinSensor.class);

    private final FileSystem fileSystem;
    private final CheckFactory checkFactory;

    public GherkinSensor(FileSystem fileSystem, CheckFactory checkFactory) {
        this.fileSystem = fileSystem;
        this.checkFactory = checkFactory;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.onlyOnLanguage(GherkinLanguage.KEY)
                .name("Gherkin Analyzer Sensor")
                .onlyOnFileType(InputFile.Type.MAIN);
    }

    @Override
    public void execute(SensorContext context) {
        // Instantiate checks via CheckFactory for @RuleProperty injection (Note 76)
        Checks<BaseCheck> checks = checkFactory.<BaseCheck>create(CheckList.REPOSITORY_KEY)
                .addAnnotatedChecks((Iterable<?>) CheckList.getAllChecks());

        List<BaseCheck> activeChecks = new ArrayList<>(checks.all());
        if (activeChecks.isEmpty()) {
            return;
        }

        // Build rule key map: annotation key string → SonarQube RuleKey
        Map<String, RuleKey> ruleKeyMap = buildRuleKeyMap(checks, activeChecks);

        FeatureParser parser = new FeatureParser();

        // Map URI → InputFile for cross-file issue routing
        Map<String, InputFile> inputFilesByUri = new HashMap<>();

        // Process each .feature file
        for (InputFile inputFile : fileSystem.inputFiles(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasType(InputFile.Type.MAIN),
                        fileSystem.predicates().hasLanguage(GherkinLanguage.KEY)))) {

            analyzeFile(context, parser, inputFile, activeChecks, checks, ruleKeyMap, inputFilesByUri);
        }

        // Finalize cross-file checks and save their issues (Note 74)
        saveCrossFileIssues(context, activeChecks, ruleKeyMap, inputFilesByUri);
    }

    /**
     * Builds a map from rule key string (from @Rule annotation) to SonarQube RuleKey.
     */
    private static Map<String, RuleKey> buildRuleKeyMap(Checks<BaseCheck> checks,
            List<BaseCheck> activeChecks) {
        Map<String, RuleKey> map = new HashMap<>();
        for (BaseCheck check : activeChecks) {
            Rule ruleAnnotation = check.getClass().getAnnotation(Rule.class);
            if (ruleAnnotation != null) {
                RuleKey rk = checks.ruleKey(check);
                if (rk != null) {
                    map.put(ruleAnnotation.key(), rk);
                }
            }
        }
        return map;
    }

    private void analyzeFile(SensorContext context, FeatureParser parser,
            InputFile inputFile, List<BaseCheck> activeChecks,
            Checks<BaseCheck> checks, Map<String, RuleKey> ruleKeyMap,
            Map<String, InputFile> inputFilesByUri) {

        String uri = inputFile.uri().toString();
        inputFilesByUri.put(uri, inputFile);

        // Read file content once (Note 72)
        String rawContent;
        try (InputStream is = inputFile.inputStream()) {
            rawContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // I/O error: log and skip (Note 78 - file-read failure)
            LOG.error("Cannot read file: " + inputFile, e);
            return;
        }

        // Parse the file
        FeatureFile featureFile;
        try {
            featureFile = parser.parse(uri, rawContent);
        } catch (IOException e) {
            // Parser I/O error: log and skip
            LOG.error("Parser error for file: " + inputFile, e);
            return;
        }

        // Check for Gherkin parse error: null feature (Note 78)
        if (featureFile.feature() == null) {
            saveParseErrorIssue(context, inputFile, ruleKeyMap);
            // Still compute metrics for the raw content (ncloc etc.)
            saveMetrics(context, inputFile, featureFile, rawContent);
            return;
        }

        // Create context with raw content for raw-content checks (Note 72)
        FeatureContext featureContext = new FeatureContext(featureFile, inputFile, rawContent);

        // Run each active check
        for (BaseCheck check : activeChecks) {
            check.setContext(featureContext);
            FeatureWalker.walk(featureFile, check);
        }

        // Save per-file issues
        saveIssues(context, inputFile, featureContext.getIssues(), ruleKeyMap);

        // Compute and save metrics
        saveMetrics(context, inputFile, featureFile, rawContent);

        // Apply syntax highlighting
        saveHighlighting(context, inputFile, featureFile);
    }

    private void saveIssues(SensorContext context, InputFile inputFile,
            List<Issue> issues, Map<String, RuleKey> ruleKeyMap) {
        for (Issue issue : issues) {
            RuleKey ruleKey = ruleKeyMap.get(issue.ruleKey());
            if (ruleKey == null) {
                continue; // rule not active in current profile
            }
            saveIssue(context, inputFile, ruleKey, issue.line(), issue.message(), issue.cost());
        }
    }

    private void saveIssue(SensorContext context, InputFile inputFile,
            RuleKey ruleKey, Integer line, String message, Double cost) {
        NewIssue newIssue = context.newIssue().forRule(ruleKey);
        NewIssueLocation location = newIssue.newLocation()
                .on(inputFile)
                .message(message);
        if (line != null && line > 0) {
            location.at(inputFile.selectLine(line));
        }
        newIssue.at(location);
        if (cost != null) {
            newIssue.gap(cost);
        }
        newIssue.save();
    }

    private void saveParseErrorIssue(SensorContext context, InputFile inputFile,
            Map<String, RuleKey> ruleKeyMap) {
        RuleKey parseErrorRuleKey = ruleKeyMap.get("parse-error");
        if (parseErrorRuleKey != null) {
            NewIssue newIssue = context.newIssue().forRule(parseErrorRuleKey);
            NewIssueLocation location = newIssue.newLocation()
                    .on(inputFile)
                    .message("This file contains a Gherkin syntax error and cannot be parsed.");
            newIssue.at(location);
            newIssue.save();
        }
    }

    private void saveCrossFileIssues(SensorContext context, List<BaseCheck> activeChecks,
            Map<String, RuleKey> ruleKeyMap, Map<String, InputFile> inputFilesByUri) {

        List<CrossFileIssue> allCrossFileIssues = new ArrayList<>();

        for (BaseCheck check : activeChecks) {
            if (check instanceof UniqueFeatureNameCheck ufc) {
                allCrossFileIssues.addAll(ufc.afterAllFiles());
            } else if (check instanceof UniqueScenarioNameCheck usc) {
                allCrossFileIssues.addAll(usc.afterAllFiles());
            } else if (check instanceof ConsistentFeatureLanguageCheck cfl) {
                allCrossFileIssues.addAll(cfl.afterAllFiles());
            }
        }

        for (CrossFileIssue cfi : allCrossFileIssues) {
            InputFile targetFile = inputFilesByUri.get(cfi.uri());
            if (targetFile == null) {
                LOG.warn("Cross-file issue for unknown URI: " + cfi.uri());
                continue;
            }
            RuleKey ruleKey = ruleKeyMap.get(cfi.ruleKey());
            if (ruleKey == null) {
                continue;
            }
            saveIssue(context, targetFile, ruleKey, cfi.line(), cfi.message(), null);
        }
    }

    private void saveMetrics(SensorContext context, InputFile inputFile,
            FeatureFile featureFile, String rawContent) {
        FeatureMetrics.MetricResult metrics = FeatureMetrics.compute(featureFile, rawContent);

        context.<Integer>newMeasure()
                .forMetric(CoreMetrics.NCLOC)
                .on(inputFile)
                .withValue(metrics.ncloc())
                .save();

        context.<Integer>newMeasure()
                .forMetric(CoreMetrics.COMMENT_LINES)
                .on(inputFile)
                .withValue(metrics.commentLines())
                .save();

        context.<Integer>newMeasure()
                .forMetric(CoreMetrics.STATEMENTS)
                .on(inputFile)
                .withValue(metrics.statements())
                .save();

        context.<Integer>newMeasure()
                .forMetric(CoreMetrics.FUNCTIONS)
                .on(inputFile)
                .withValue(metrics.functions())
                .save();

        context.<Integer>newMeasure()
                .forMetric(CoreMetrics.CLASSES)
                .on(inputFile)
                .withValue(metrics.classes())
                .save();
    }

    private void saveHighlighting(SensorContext context, InputFile inputFile,
            FeatureFile featureFile) {
        List<FeatureHighlighter.HighlightRange> ranges = FeatureHighlighter.highlight(featureFile);
        if (ranges.isEmpty()) {
            return;
        }

        NewHighlighting highlighting = context.newHighlighting().onFile(inputFile);

        for (FeatureHighlighter.HighlightRange range : ranges) {
            TypeOfText type = mapHighlightType(range.type());
            try {
                highlighting.highlight(
                        inputFile.newRange(
                                range.startLine(), range.startColumn() - 1,
                                range.endLine(), range.endColumn()),
                        type);
            } catch (Exception e) {
                // Skip invalid ranges (can happen with edge cases like empty keywords)
                LOG.debug("Skipping invalid highlight range: " + range, e);
            }
        }

        highlighting.save();
    }

    private static TypeOfText mapHighlightType(FeatureHighlighter.HighlightType type) {
        return switch (type) {
            case KEYWORD -> TypeOfText.KEYWORD;
            case ANNOTATION -> TypeOfText.ANNOTATION;
            case COMMENT -> TypeOfText.COMMENT;
        };
    }
}
