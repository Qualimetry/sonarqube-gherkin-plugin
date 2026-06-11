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

import com.qualimetry.sonar.gherkin.analyzer.checks.FeatureFileRequiredCheck;
import com.qualimetry.sonar.gherkin.analyzer.checks.ParseErrorCheck;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.rule.RuleKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class GherkinSensorTest {

    @Test
    void shouldDescribeWithCorrectName() {
        FileSystem fileSystem = mock(FileSystem.class);
        CheckFactory checkFactory = mock(CheckFactory.class);
        GherkinSensor sensor = new GherkinSensor(fileSystem, checkFactory);

        SensorDescriptor descriptor = mock(SensorDescriptor.class);
        when(descriptor.onlyOnLanguage(anyString())).thenReturn(descriptor);
        when(descriptor.name(anyString())).thenReturn(descriptor);
        when(descriptor.onlyOnFileType(any(InputFile.Type.class))).thenReturn(descriptor);

        sensor.describe(descriptor);

        verify(descriptor).onlyOnLanguage("gherkin");
        verify(descriptor).name("Gherkin Analyzer Sensor");
        verify(descriptor).onlyOnFileType(InputFile.Type.MAIN);
    }

    @Test
    void shouldHandleEmptyProject() {
        FileSystem fileSystem = mock(FileSystem.class);
        when(fileSystem.inputFiles(any())).thenReturn(Collections.emptyList());
        when(fileSystem.predicates()).thenReturn(mock(org.sonar.api.batch.fs.FilePredicates.class));

        CheckFactory checkFactory = mock(CheckFactory.class);
        Checks<BaseCheck> checks = mock(Checks.class);
        when(checkFactory.<BaseCheck>create(anyString())).thenReturn(checks);
        when(checks.addAnnotatedChecks(any(Iterable.class))).thenReturn(checks);
        when(checks.all()).thenReturn(Collections.emptyList());

        SensorContext context = mock(SensorContext.class);
        when(context.fileSystem()).thenReturn(fileSystem);

        GherkinSensor sensor = new GherkinSensor(fileSystem, checkFactory);
        // Should complete without exception when no checks are active
        sensor.execute(context);
    }

    @Test
    void shouldCreateSensorWithCorrectDependencies() {
        FileSystem fileSystem = mock(FileSystem.class);
        CheckFactory checkFactory = mock(CheckFactory.class);

        // Verify sensor can be instantiated (constructor injection pattern)
        GherkinSensor sensor = new GherkinSensor(fileSystem, checkFactory);

        // Verify describe works
        SensorDescriptor descriptor = mock(SensorDescriptor.class);
        when(descriptor.onlyOnLanguage(anyString())).thenReturn(descriptor);
        when(descriptor.name(anyString())).thenReturn(descriptor);
        when(descriptor.onlyOnFileType(any(InputFile.Type.class))).thenReturn(descriptor);

        sensor.describe(descriptor);

        verify(descriptor).onlyOnLanguage(GherkinLanguage.KEY);
    }

    @Test
    void shouldRunChecksOnParsedButFeaturelessFile() throws IOException {
        List<String> savedRuleKeys = runSensorOn("# a comment-only file with no Feature\n");

        assertThat(savedRuleKeys)
                .contains("feature-file-required")
                .doesNotContain("parse-error");
    }

    @Test
    void shouldEmitParseErrorOnSyntaxError() throws IOException {
        List<String> savedRuleKeys = runSensorOn(
                "Feature: One\n\n  Scenario: First\n    Given a step\n\nFeature: Two\n");

        assertThat(savedRuleKeys)
                .contains("parse-error")
                .doesNotContain("feature-file-required");
    }

    private static List<String> runSensorOn(String content) throws IOException {
        InputFile inputFile = mock(InputFile.class);
        when(inputFile.uri()).thenReturn(URI.create("file:///test/sample.feature"));
        when(inputFile.inputStream()).thenReturn(
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        FileSystem fileSystem = mock(FileSystem.class);
        when(fileSystem.predicates()).thenReturn(mock(org.sonar.api.batch.fs.FilePredicates.class));
        when(fileSystem.inputFiles(any())).thenReturn(List.of(inputFile));

        Checks<BaseCheck> checks = mock(Checks.class);
        CheckFactory checkFactory = mock(CheckFactory.class);
        when(checkFactory.<BaseCheck>create(anyString())).thenReturn(checks);
        when(checks.addAnnotatedChecks(any(Iterable.class))).thenReturn(checks);
        when(checks.all()).thenReturn(List.of(new FeatureFileRequiredCheck(), new ParseErrorCheck()));
        when(checks.ruleKey(any(BaseCheck.class))).thenAnswer(inv -> {
            BaseCheck check = inv.getArgument(0);
            String key = check.getClass().getAnnotation(org.sonar.check.Rule.class).key();
            return RuleKey.of("gherkin", key);
        });

        List<String> savedRuleKeys = new ArrayList<>();
        SensorContext context = mock(SensorContext.class);
        when(context.newIssue()).thenAnswer(inv -> {
            NewIssue newIssue = mock(NewIssue.class);
            NewIssueLocation location = mock(NewIssueLocation.class);
            when(location.on(any())).thenReturn(location);
            when(location.message(anyString())).thenReturn(location);
            when(newIssue.newLocation()).thenReturn(location);
            when(newIssue.at(any())).thenReturn(newIssue);
            when(newIssue.gap(any())).thenReturn(newIssue);
            when(newIssue.forRule(any())).thenAnswer(ruleInv -> {
                RuleKey rk = ruleInv.getArgument(0);
                savedRuleKeys.add(rk.rule());
                return newIssue;
            });
            return newIssue;
        });
        when(context.newMeasure()).thenAnswer(inv -> {
            NewMeasure measure = mock(NewMeasure.class);
            when(measure.forMetric(any())).thenReturn(measure);
            when(measure.on(any())).thenReturn(measure);
            when(measure.withValue(any())).thenReturn(measure);
            return measure;
        });
        when(context.newHighlighting()).thenAnswer(inv -> {
            NewHighlighting highlighting = mock(NewHighlighting.class);
            when(highlighting.onFile(any())).thenReturn(highlighting);
            return highlighting;
        });

        new GherkinSensor(fileSystem, checkFactory).execute(context);
        return savedRuleKeys;
    }
}
