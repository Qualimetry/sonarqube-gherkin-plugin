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

import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import java.util.Collections;

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
}
