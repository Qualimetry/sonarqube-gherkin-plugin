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

import com.qualimetry.sonar.gherkin.analyzer.checks.CheckList;
import com.qualimetry.sonar.gherkin.analyzer.visitor.BaseCheck;
import org.junit.jupiter.api.Test;
import org.sonar.check.Rule;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Generates the evidence report artifact and verifies that all annotated
 * test fixtures produce expected results.
 * <p>
 * Running this test produces a {@code target/evidence-report/} directory
 * containing:
 * <ul>
 *   <li>{@code index.html} — self-contained HTML report for human review</li>
 *   <li>{@code rules/<ruleKey>/} — per-rule directories with fixture files,
 *       description HTML, and machine-readable {@code results.json}</li>
 * </ul>
 * <p>
 * The directory can be zipped and shared with third-party reviewers for
 * both independent-authorship and test-correctness verification.
 */
class EvidenceReportTest {

    /**
     * Severity per rule key, mirrored from GherkinRulesDefinition for
     * evidence report metadata. This is test-only data; the source of
     * truth for production severity lives in the plugin module.
     */
    private static final Map<String, String> SEVERITIES = Map.ofEntries(
            Map.entry("feature-file-required", "MAJOR"),
            Map.entry("feature-name-required", "CRITICAL"),
            Map.entry("feature-description-recommended", "INFO"),
            Map.entry("scenario-required", "MAJOR"),
            Map.entry("scenario-name-required", "CRITICAL"),
            Map.entry("step-required", "MAJOR"),
            Map.entry("examples-minimum-rows", "CRITICAL"),
            Map.entry("examples-column-coverage", "CRITICAL"),
            Map.entry("scenario-count-limit", "MAJOR"),
            Map.entry("step-count-limit", "MAJOR"),
            Map.entry("background-given-only", "CRITICAL"),
            Map.entry("shared-given-to-background", "MAJOR"),
            Map.entry("step-order-given-when-then", "CRITICAL"),
            Map.entry("single-when-per-scenario", "MAJOR"),
            Map.entry("when-then-required", "CRITICAL"),
            Map.entry("unique-feature-name", "MAJOR"),
            Map.entry("unique-scenario-name", "MAJOR"),
            Map.entry("no-duplicate-steps", "CRITICAL"),
            Map.entry("use-scenario-outline-for-examples", "MAJOR"),
            Map.entry("business-language-only", "MAJOR"),
            Map.entry("consistent-feature-language", "MAJOR"),
            Map.entry("consistent-indentation", "MINOR"),
            Map.entry("no-tab-characters", "MINOR"),
            Map.entry("no-trailing-whitespace", "MINOR"),
            Map.entry("newline-at-end-of-file", "MINOR"),
            Map.entry("no-byte-order-mark", "MAJOR"),
            Map.entry("consistent-line-endings", "MINOR"),
            Map.entry("file-name-convention", "MINOR"),
            Map.entry("comment-format", "MINOR"),
            Map.entry("prefer-and-but-keywords", "MINOR"),
            Map.entry("no-star-step-prefix", "MINOR"),
            Map.entry("examples-separator-line", "MINOR"),
            Map.entry("step-sentence-max-length", "MAJOR"),
            Map.entry("tag-name-pattern", "MINOR"),
            Map.entry("tag-permitted-values", "MINOR"),
            Map.entry("tag-placement", "MINOR"),
            Map.entry("no-redundant-tags", "MINOR"),
            Map.entry("no-examples-tags", "MINOR"),
            Map.entry("no-unused-variables", "MAJOR"),
            Map.entry("given-step-pattern", "MINOR"),
            Map.entry("when-step-pattern", "MINOR"),
            Map.entry("then-step-pattern", "MINOR"),
            Map.entry("no-unknown-step-type", "MAJOR"),
            Map.entry("todo-comment", "INFO"),
            Map.entry("fixme-comment", "INFO"),
            Map.entry("comment-pattern-match", "MAJOR"),
            Map.entry("spelling-accuracy", "INFO"),
            Map.entry("parse-error", "CRITICAL"),
            Map.entry("rule-name-required", "CRITICAL"),
            Map.entry("rule-scenario-required", "MAJOR"),
            Map.entry("unique-rule-name", "MAJOR"),
            Map.entry("rule-description-recommended", "INFO"),
            Map.entry("outline-placeholder-required", "MAJOR"),
            Map.entry("scenario-outline-requires-examples", "MAJOR"),
            Map.entry("background-needs-multiple-scenarios", "MINOR"),
            Map.entry("blank-line-before-scenario", "MINOR"),
            Map.entry("rule-scenario-count-limit", "MAJOR"),
            Map.entry("feature-rule-count-limit", "MAJOR"),
            Map.entry("no-redundant-rule-tags", "MINOR"),
            Map.entry("rule-tag-placement", "MINOR"),
            Map.entry("examples-name-when-multiple", "MINOR"),
            Map.entry("consistent-scenario-keyword", "MINOR"),
            Map.entry("no-duplicate-tags", "MINOR"),
            Map.entry("no-multiple-empty-lines", "MINOR"),
            Map.entry("required-tags", "MAJOR"),
            Map.entry("no-restricted-tags", "MAJOR"),
            Map.entry("name-max-length", "MINOR"),
            Map.entry("one-space-between-tags", "MINOR"),
            Map.entry("no-partially-commented-tag-lines", "MINOR"),
            Map.entry("outline-single-example-row", "MINOR"),
            Map.entry("no-restricted-patterns", "MAJOR"),
            Map.entry("max-tags-per-element", "MINOR"),
            Map.entry("feature-file-max-lines", "MINOR"),
            Map.entry("data-table-max-columns", "MINOR"),
            Map.entry("unique-examples-headers", "CRITICAL"),
            Map.entry("no-empty-examples-cells", "MAJOR"),
            Map.entry("no-duplicate-scenario-bodies", "MAJOR"),
            Map.entry("no-conflicting-tags", "MAJOR"),
            Map.entry("no-commented-out-steps", "MINOR"),
            Map.entry("background-step-count-limit", "MAJOR"),
            Map.entry("feature-name-matches-filename", "MINOR"),
            Map.entry("scenario-description-recommended", "INFO"),
            Map.entry("no-empty-doc-strings", "MINOR")
    );

    @Test
    void generateEvidenceReport() throws Exception {
        Set<String> defaultRuleKeys = Set.copyOf(CheckList.getDefaultRuleKeys());
        List<RuleEvidence> allEvidence = new ArrayList<>();

        for (Class<? extends BaseCheck> checkClass : CheckList.getAllChecks()) {
            RuleEvidence evidence = collectEvidenceForCheck(checkClass, defaultRuleKeys);
            allEvidence.add(evidence);
        }

        // Generate the report
        Path outputDir = Path.of("target", "evidence-report");
        EvidenceReportGenerator.generate(allEvidence, outputDir);

        // Print summary
        long totalRules = allEvidence.size();
        long verifiedRules = allEvidence.stream()
                .filter(RuleEvidence::allAcceptable).count();
        long totalFixtures = allEvidence.stream()
                .mapToInt(RuleEvidence::fixtureCount).sum();
        long passedFixtures = allEvidence.stream()
                .mapToLong(RuleEvidence::passedCount).sum();
        long failedFixtures = allEvidence.stream()
                .mapToLong(RuleEvidence::failedCount).sum();

        System.out.println("=== Evidence Report Generated ===");
        System.out.println("Output: " + outputDir.toAbsolutePath());
        System.out.println("Rules: " + verifiedRules + "/" + totalRules + " verified");
        System.out.println("Fixtures: " + passedFixtures + "/" + totalFixtures + " passed"
                + (failedFixtures > 0 ? ", " + failedFixtures + " FAILED" : ""));

        // Assert the artifact was created correctly
        assertThat(outputDir.resolve("index.html")).exists();
        assertThat(allEvidence).hasSize(CheckList.getAllChecks().size());

        // Every rule must have at least one fixture file discovered
        for (RuleEvidence evidence : allEvidence) {
            assertThat(evidence.fixtureResults())
                    .as("Fixtures for rule " + evidence.ruleKey())
                    .isNotEmpty();
        }

        // Assert zero genuine failures — every fixture must be acceptable
        List<String> genuineFailures = allEvidence.stream()
                .flatMap(e -> e.fixtureResults().stream()
                        .filter(f -> FixtureResult.STATUS_FAIL.equals(f.status()))
                        .map(f -> e.ruleKey() + "/" + f.fixtureName()
                                + ": " + String.join("; ", f.mismatches())))
                .toList();
        assertThat(genuineFailures)
                .as("Genuine fixture verification failures")
                .isEmpty();

        // Zip the evidence pack and place it in the project root for easy sharing.
        // The zip is overwritten on every test run so it's always current.
        Path zipFile = zipEvidenceReport(outputDir);
        System.out.println("Evidence pack: " + zipFile.toAbsolutePath());
    }

    /**
     * Zips the {@code target/evidence-report/} directory into a single
     * {@code .zip} file placed at the project root (two levels up from
     * the analyzer module target directory). The zip is named
     * {@code gherkin-evidence-report.zip} and is overwritten on every run.
     *
     * @param evidenceDir the evidence report directory to zip
     * @return the path to the created zip file
     */
    private static Path zipEvidenceReport(Path evidenceDir) throws IOException {
        // Place the zip in the gherkin project root (plugin/../)
        // so it sits alongside DEPLOYMENT.md and is easy to find.
        Path projectRoot = evidenceDir.toAbsolutePath()
                .getParent()   // target
                .getParent()   // gherkin-analyzer
                .getParent();  // plugin (project root)
        Path zipFile = projectRoot.resolve("gherkin-evidence-report.zip");

        // Delete existing zip so it's always fresh
        Files.deleteIfExists(zipFile);

        try (OutputStream fos = Files.newOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.setLevel(9);
            Path base = evidenceDir;
            Files.walkFileTree(base, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    String entryName = "evidence-report/"
                            + base.relativize(file).toString().replace('\\', '/');
                    zos.putNextEntry(new ZipEntry(entryName));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    if (!dir.equals(base)) {
                        String entryName = "evidence-report/"
                                + base.relativize(dir).toString().replace('\\', '/') + "/";
                        zos.putNextEntry(new ZipEntry(entryName));
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return zipFile;
    }

    /**
     * Returns the fixture result as-is. All fixtures should be PASS with
     * sidecar {@code .properties} files providing check configuration and
     * the {@code # Noncompliant:file} annotation supporting file-level issues.
     * Any non-PASS result is a genuine failure.
     */
    private static FixtureResult classifyFixture(FixtureResult raw) {
        return raw;
    }

    /**
     * Applies configuration from a sidecar {@code .properties} file to the
     * check instance. The sidecar file maps {@code @RuleProperty} key names
     * to their values. Properties are applied via public setter methods on
     * the check class (e.g., property {@code maxScenarios} invokes
     * {@code setMaxScenarios()}).
     */
    private static void applyFixtureConfig(BaseCheck check, String fixturePath) {
        Map<String, String> config = CheckTestUtils.readFixtureConfig(fixturePath);
        if (config.isEmpty()) return;

        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String setterName = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
            try {
                // Try String setter
                Method setter = check.getClass().getMethod(setterName, String.class);
                setter.invoke(check, value);
            } catch (NoSuchMethodException e1) {
                try {
                    // Try int setter
                    Method setter = check.getClass().getMethod(setterName, int.class);
                    setter.invoke(check, Integer.parseInt(value));
                } catch (Exception e2) {
                    throw new RuntimeException(
                            "Cannot apply fixture config " + key + "=" + value
                                    + " to " + check.getClass().getSimpleName(), e2);
                }
            } catch (Exception e) {
                throw new RuntimeException(
                        "Cannot apply fixture config " + key + "=" + value
                                + " to " + check.getClass().getSimpleName(), e);
            }
        }
    }

    private RuleEvidence collectEvidenceForCheck(
            Class<? extends BaseCheck> checkClass, Set<String> defaultRuleKeys)
            throws Exception {

        // Extract rule key from @Rule annotation
        Rule ruleAnnotation = checkClass.getAnnotation(Rule.class);
        String ruleKey = ruleAnnotation != null ? ruleAnnotation.key() : "unknown";

        // Build metadata
        String displayName = CheckTestUtils.toDisplayName(ruleKey);
        String severity = SEVERITIES.getOrDefault(ruleKey, "MAJOR");
        boolean defaultActive = defaultRuleKeys.contains(ruleKey);
        String descriptionHtml = CheckTestUtils.readRuleDescriptionHtml(ruleKey);

        // Discover fixture files
        List<String> fixturePaths = CheckTestUtils.discoverFixturesForRule(ruleKey);

        // Collect evidence for each fixture, applying sidecar config and classifying
        List<FixtureResult> fixtureResults = new ArrayList<>();
        for (String fixturePath : fixturePaths) {
            BaseCheck check = checkClass.getDeclaredConstructor().newInstance();
            applyFixtureConfig(check, fixturePath);
            FixtureResult raw = CheckVerifier.collectEvidence(check, fixturePath);
            fixtureResults.add(classifyFixture(raw));
        }

        return new RuleEvidence(ruleKey, displayName, severity, defaultActive,
                descriptionHtml, fixtureResults);
    }
}
