# Changelog

All notable changes to the Qualimetry Gherkin Analyzer for SonarQube are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.3.3] - 2026-03-02

### Fixed

- **no-multiple-empty-lines**: Issue was reported on the wrong line (first blank instead of the extra blank). Now correctly reports the redundant blank line (1-based line number fix).

### Changed

- **Pattern-based rules** (comment-pattern-match, required-tags, tag-name-pattern, file-name-convention, no-restricted-patterns): Invalid regex in configuration no longer crashes analysis; a file-level issue is reported and the check is skipped for that file.
- **use-scenario-outline-for-examples** and **consistent-scenario-keyword**: GherkinDialectProvider is now cached (static) instead of instantiated per visit.
- Rule documentation: tag-name-pattern (removed compliant example from noncompliant section), tag-placement and rule-tag-placement (noncompliant comment moved to repeated tag line), spelling-accuracy (Parameters table), max-tags-per-element (Feature block in examples), required-tags (note on direct vs inherited tags).

## [1.3.2] - 2026-02-28

### Changed

- Minor text and documentation updates.

## [1.3.1] - 2026-02-22

### Changed

- Rule tags updated to SonarQube built-in tags (`gherkin`, `convention`, `design`, `suspicious`, `unused`, `pitfall`) for better discoverability and filtering.

## [1.3.0] - 2026-02-19

### Changed

- Minor text and documentation updates.

## [1.2.2] - 2026-02-17

### Changed

- Minor text and documentation updates.

## [1.2.1] - 2026-02-16

### Changed

- Release versioning aligned with GitHub releases.
- Minor text and documentation updates.

## [1.2.0] - 2026-02-16

### Added

- **9 new analysis rules** (rules 75–83), bringing the total to **83 rules**:
  - `unique-examples-headers` (CRITICAL) – Examples table column headers must be unique.
  - `no-empty-examples-cells` (MAJOR) – Examples table data cells must not be empty.
  - `no-duplicate-scenario-bodies` (MAJOR) – Scenarios within the same scope must not have identical step sequences.
  - `no-conflicting-tags` (MAJOR) – Configurable mutually exclusive tag pair detection.
  - `no-commented-out-steps` (MINOR) – Detect dead step definitions in comments.
  - `background-step-count-limit` (MAJOR) – Background sections must not exceed configurable step count (default: 5).
  - `feature-name-matches-filename` (MINOR) – Feature names should correspond to file names.
  - `scenario-description-recommended` (INFO) – Scenarios should include descriptions.
  - `no-empty-doc-strings` (MINOR) – Doc strings must not be empty.

### Changed

- Quality profile updated: **53 rules active** out of 83 (was 50/74).

## [1.1.0] - 2026-02-14

### Added

- **26 new analysis rules** (rules 49–74), bringing the total to **74 rules**, in six categories: Rule Block Quality, Structural Integrity, Rule-Scoped Best Practices, Advanced Quality, Ecosystem Parity, and Configurable Quality Thresholds.
- Quality profile updated: 50 rules active out of 74 (was 38/48).

### Fixed

- `consistent-indentation`: Supports all 70+ Gherkin languages and correct indentation for Rule block nesting.
- `use-scenario-outline-for-examples`: Language-aware keyword comparison; no false positives on non-English Scenario Outlines.
- `shared-given-to-background`: Scoped to each container (Feature or Rule) independently.
- `no-redundant-tags` and `tag-placement`: Rule-level tag handling corrected.
- `no-star-step-prefix`: Severity reduced to MINOR and deactivated by default (specification-endorsed syntax).

## [1.0.0] - 2026-01-01

### Added

- Initial release of the Gherkin Analyzer plugin for SonarQube.
- **48 analysis rules** covering structure, design, style and convention, tags, variables and data, step patterns, comments and markers, spelling, and parser errors.
- **"Qualimetry Gherkin" quality profile** with 38 rules active by default.
- Uses the official Cucumber Gherkin parser (v28).
- Syntax highlighting and code metrics (NCLOC, comment lines, statements, functions, classes).
- Compatible with SonarQube Server 2025.1 LTA and later (Plugin API 13.x).
- Licensed under the Apache License, Version 2.0.
