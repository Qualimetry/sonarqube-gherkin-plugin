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
package com.qualimetry.sonar.gherkin.analyzer.parser;

import com.qualimetry.sonar.gherkin.analyzer.parser.model.BackgroundDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.Comment;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.DataTableDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.DocStringDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ExamplesDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.FeatureFile;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.RuleDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.ScenarioDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.StepDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TagDefinition;
import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.DataTable;
import io.cucumber.messages.types.DocString;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Location;
import io.cucumber.messages.types.Rule;
import io.cucumber.messages.types.RuleChild;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Step;
import io.cucumber.messages.types.StepKeywordType;
import io.cucumber.messages.types.TableCell;
import io.cucumber.messages.types.TableRow;
import io.cucumber.messages.types.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses Gherkin {@code .feature} files using the official Cucumber parser
 * ({@code io.cucumber:gherkin}) and converts the resulting message types
 * into the internal tree model used by the analyzer.
 * <p>
 * This class is stateless and thread-safe. A single instance can be reused
 * across multiple file parses.
 */
public class FeatureParser {

    private static final io.cucumber.gherkin.GherkinParser GHERKIN_PARSER =
            io.cucumber.gherkin.GherkinParser.builder()
                    .includeGherkinDocument(true)
                    .includePickles(false)
                    .includeSource(false)
                    .build();

    /**
     * Parses a Gherkin feature file from a string content.
     * <p>
     * This is a convenience overload that wraps the string in a
     * {@link ByteArrayInputStream} and delegates to {@link #parse(String, InputStream)}.
     *
     * @param uri     the URI or path identifying the file
     * @param content the file content as a string
     * @return a {@link FeatureFile} representing the parsed tree
     * @throws IOException if an I/O error occurs during parsing
     */
    public FeatureFile parse(String uri, String content) throws IOException {
        return parse(uri, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Parses a Gherkin feature file from the given input stream.
     *
     * @param uri     the URI or path identifying the file
     * @param content the file content as an input stream
     * @return a {@link FeatureFile} representing the parsed tree; the
     *         {@link FeatureFile#feature()} will be {@code null} if the file
     *         is empty, contains only comments, or has parse errors
     * @throws IOException if reading the input stream fails
     */
    public FeatureFile parse(String uri, InputStream content) throws IOException {
        List<Envelope> envelopes = GHERKIN_PARSER.parse(uri, content).toList();

        GherkinDocument doc = envelopes.stream()
                .flatMap(e -> e.getGherkinDocument().stream())
                .findFirst()
                .orElse(null);

        if (doc == null) {
            // Parse error: no GherkinDocument was produced by the Cucumber parser.
            // This happens when the file contains syntax errors that prevent
            // the parser from constructing any document at all.
            return new FeatureFile(null, List.of(), "en", uri);
        }

        FeatureDefinition feature = doc.getFeature()
                .map(this::convertFeature)
                .orElse(null);

        List<Comment> comments = doc.getComments().stream()
                .map(this::convertComment)
                .toList();

        String language = doc.getFeature()
                .map(Feature::getLanguage)
                .orElse("en");

        return new FeatureFile(feature, comments, language, uri);
    }

    // ----------------------------------------------------------------
    // Conversion methods: Cucumber message types -> internal model
    // ----------------------------------------------------------------

    private FeatureDefinition convertFeature(Feature feature) {
        TextPosition position = convertLocation(feature.getLocation());
        List<TagDefinition> tags = feature.getTags().stream()
                .map(this::convertTag)
                .toList();

        BackgroundDefinition background = null;
        List<ScenarioDefinition> scenarios = new ArrayList<>();
        List<RuleDefinition> rules = new ArrayList<>();

        for (FeatureChild child : feature.getChildren()) {
            if (child.getBackground().isPresent()) {
                background = convertBackground(child.getBackground().get());
            } else if (child.getScenario().isPresent()) {
                scenarios.add(convertScenario(child.getScenario().get()));
            } else if (child.getRule().isPresent()) {
                rules.add(convertRule(child.getRule().get()));
            }
        }

        return new FeatureDefinition(
                position,
                feature.getKeyword(),
                feature.getLanguage(),
                feature.getName(),
                feature.getDescription(),
                tags,
                background,
                scenarios,
                rules);
    }

    private ScenarioDefinition convertScenario(Scenario scenario) {
        TextPosition position = convertLocation(scenario.getLocation());
        List<TagDefinition> tags = scenario.getTags().stream()
                .map(this::convertTag)
                .toList();
        List<StepDefinition> steps = scenario.getSteps().stream()
                .map(this::convertStep)
                .toList();
        List<ExamplesDefinition> examples = scenario.getExamples().stream()
                .map(this::convertExamples)
                .toList();
        boolean isOutline = !examples.isEmpty();

        return new ScenarioDefinition(
                position,
                scenario.getKeyword(),
                scenario.getName(),
                scenario.getDescription(),
                tags,
                steps,
                examples,
                isOutline);
    }

    private BackgroundDefinition convertBackground(Background background) {
        TextPosition position = convertLocation(background.getLocation());
        List<StepDefinition> steps = background.getSteps().stream()
                .map(this::convertStep)
                .toList();

        return new BackgroundDefinition(
                position,
                background.getKeyword(),
                background.getName(),
                background.getDescription(),
                steps);
    }

    private RuleDefinition convertRule(Rule rule) {
        TextPosition position = convertLocation(rule.getLocation());
        List<TagDefinition> tags = rule.getTags().stream()
                .map(this::convertTag)
                .toList();

        BackgroundDefinition background = null;
        List<ScenarioDefinition> scenarios = new ArrayList<>();

        for (RuleChild child : rule.getChildren()) {
            if (child.getBackground().isPresent()) {
                background = convertBackground(child.getBackground().get());
            } else if (child.getScenario().isPresent()) {
                scenarios.add(convertScenario(child.getScenario().get()));
            }
        }

        return new RuleDefinition(
                position,
                rule.getKeyword(),
                rule.getName(),
                rule.getDescription(),
                tags,
                background,
                scenarios);
    }

    private StepDefinition convertStep(Step step) {
        TextPosition position = convertLocation(step.getLocation());
        String keywordType = step.getKeywordType()
                .map(StepKeywordType::name)
                .orElse("UNKNOWN");
        DataTableDefinition dataTable = step.getDataTable()
                .map(this::convertDataTable)
                .orElse(null);
        DocStringDefinition docString = step.getDocString()
                .map(this::convertDocString)
                .orElse(null);

        return new StepDefinition(
                position,
                step.getKeyword(),
                keywordType,
                step.getText(),
                dataTable,
                docString);
    }

    private TagDefinition convertTag(Tag tag) {
        TextPosition position = convertLocation(tag.getLocation());
        String name = tag.getName();
        if (name.startsWith("@")) {
            name = name.substring(1);
        }
        return new TagDefinition(position, name);
    }

    private ExamplesDefinition convertExamples(Examples examples) {
        TextPosition position = convertLocation(examples.getLocation());
        List<TagDefinition> tags = examples.getTags().stream()
                .map(this::convertTag)
                .toList();

        DataTableDefinition table = null;
        if (examples.getTableHeader().isPresent()) {
            TableRow header = examples.getTableHeader().get();
            List<List<String>> rows = new ArrayList<>();
            rows.add(header.getCells().stream()
                    .map(TableCell::getValue)
                    .toList());
            for (TableRow bodyRow : examples.getTableBody()) {
                rows.add(bodyRow.getCells().stream()
                        .map(TableCell::getValue)
                        .toList());
            }
            TextPosition tablePosition = convertLocation(header.getLocation());
            table = new DataTableDefinition(tablePosition, rows);
        }

        return new ExamplesDefinition(
                position,
                examples.getKeyword(),
                examples.getName(),
                examples.getDescription(),
                tags,
                table);
    }

    private DataTableDefinition convertDataTable(DataTable dataTable) {
        TextPosition position = convertLocation(dataTable.getLocation());
        List<List<String>> rows = dataTable.getRows().stream()
                .map(row -> row.getCells().stream()
                        .map(TableCell::getValue)
                        .toList())
                .toList();

        return new DataTableDefinition(position, rows);
    }

    private DocStringDefinition convertDocString(DocString docString) {
        TextPosition position = convertLocation(docString.getLocation());
        String contentType = docString.getMediaType().orElse("");

        return new DocStringDefinition(
                position,
                contentType,
                docString.getContent(),
                docString.getDelimiter());
    }

    private Comment convertComment(io.cucumber.messages.types.Comment cucumberComment) {
        TextPosition position = convertLocation(cucumberComment.getLocation());
        return new Comment(position, cucumberComment.getText());
    }

    private TextPosition convertLocation(Location location) {
        int line = Math.toIntExact(location.getLine());
        int column = location.getColumn().map(Math::toIntExact).orElse(1);
        return new TextPosition(line, column);
    }
}
