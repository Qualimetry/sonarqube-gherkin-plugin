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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class GherkinRulesDefinitionTest {

    private static RulesDefinition.Repository repository;

    @BeforeAll
    static void setUp() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        new GherkinRulesDefinition().define(context);
        repository = context.repository(CheckList.REPOSITORY_KEY);
    }

    @Test
    void shouldCreateRepository() {
        assertThat(repository).isNotNull();
    }

    @Test
    void shouldHaveCorrectRepositoryKey() {
        assertThat(repository.key()).isEqualTo("qualimetry-gherkin");
    }

    @Test
    void shouldHaveCorrectRepositoryName() {
        assertThat(repository.name()).isEqualTo("Qualimetry Gherkin");
    }

    @Test
    void shouldHaveCorrectLanguage() {
        assertThat(repository.language()).isEqualTo("gherkin");
    }

    @Test
    void shouldLoad74Rules() {
        assertThat(repository.rules()).hasSize(83);
    }

    @Test
    void shouldHaveHtmlDescriptionForEveryRule() {
        for (RulesDefinition.Rule rule : repository.rules()) {
            assertThat(rule.htmlDescription())
                    .as("Missing HTML description for rule: " + rule.key())
                    .isNotNull()
                    .isNotEmpty();
        }
    }

    @Test
    void shouldHaveSeverityForEveryRule() {
        for (RulesDefinition.Rule rule : repository.rules()) {
            assertThat(rule.severity())
                    .as("Missing severity for rule: " + rule.key())
                    .isNotNull()
                    .isNotEmpty();
        }
    }

    @Test
    void shouldHaveRemediationFunctionForEveryRule() {
        for (RulesDefinition.Rule rule : repository.rules()) {
            assertThat(rule.debtRemediationFunction())
                    .as("Missing remediation function for rule: " + rule.key())
                    .isNotNull();
        }
    }

    @Test
    void shouldHaveHumanReadableNameForEveryRule() {
        for (RulesDefinition.Rule rule : repository.rules()) {
            // Names should not equal the kebab-case key (which is the default when @Rule has no name)
            assertThat(rule.name())
                    .as("Rule name should be human-readable, not kebab-case key: " + rule.key())
                    .isNotNull()
                    .isNotEqualTo(rule.key());
            // Names should contain at least one space (they are multi-word titles)
            assertThat(rule.name())
                    .as("Rule name should be a multi-word title: " + rule.key())
                    .contains(" ");
        }
    }

    @Test
    void shouldHaveCorrectSeverityForCriticalRules() {
        assertThat(repository.rule("feature-name-required").severity()).isEqualTo("CRITICAL");
        assertThat(repository.rule("parse-error").severity()).isEqualTo("CRITICAL");
        assertThat(repository.rule("background-given-only").severity()).isEqualTo("CRITICAL");
    }

    @Test
    void shouldHaveCorrectSeverityForInfoRules() {
        assertThat(repository.rule("feature-description-recommended").severity()).isEqualTo("INFO");
        assertThat(repository.rule("todo-comment").severity()).isEqualTo("INFO");
        assertThat(repository.rule("fixme-comment").severity()).isEqualTo("INFO");
        assertThat(repository.rule("spelling-accuracy").severity()).isEqualTo("INFO");
    }

    @Test
    void shouldHaveCorrectSeverityForMinorRules() {
        assertThat(repository.rule("consistent-indentation").severity()).isEqualTo("MINOR");
        assertThat(repository.rule("no-tab-characters").severity()).isEqualTo("MINOR");
        assertThat(repository.rule("no-trailing-whitespace").severity()).isEqualTo("MINOR");
        assertThat(repository.rule("no-star-step-prefix").severity()).isEqualTo("MINOR");
    }

    @Test
    void shouldHaveTagsForEveryRule() {
        for (RulesDefinition.Rule rule : repository.rules()) {
            assertThat(rule.tags())
                    .as("Rule should have tags: " + rule.key())
                    .isNotNull()
                    .isNotEmpty();
            assertThat(rule.tags()).contains("gherkin");
        }
    }

}
