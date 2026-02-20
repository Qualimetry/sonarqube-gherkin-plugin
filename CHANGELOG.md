# Changelog

All notable changes to the Qualimetry Gherkin Analyzer are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.3.0] - 2026-02-19

### Added

- **Import rules from SonarQube** – Command to pull your Gherkin quality profile (rules and severities) into workspace or user settings. URL and profile are remembered; token is never stored. Bearer then Basic auth for compatibility.
- **Rule customization mode** – Setting `gherkinAnalyzer.rulesReplaceDefaults`: when `true` (e.g. after import), only listed rules run; when `false`, listed rules are overrides and unlisted rules use the default profile.
- **Precise diagnostics** – Rules such as business-language-only now underline only the offending word (e.g. "click", "submit"), not the whole line or a single character.

### Changed

- **Import UX** – Progress dialog dismisses when import finishes. Success message shows rule count and directs users to File > Preferences to view settings (no Open Settings button).
- **Version alignment** – SonarQube plugin and VS Code extension versions kept in sync from root POM; package script runs align and check before packaging.

## [1.2.2] - 2026-02-17

### Changed

- VS Code extension: README in package tailored for Marketplace users (no CI/build/install instructions; configuration clearly optional).

## [1.2.1] - 2026-02-16

### Changed

- Release versioning: version is now incremented for each GitHub release (no duplicate release numbers).
- VS Code extension: display name "Qualimetry Gherkin Analyzer"; package id `qualimetry-vscode-gherkin-plugin`; VSIX filename `qualimetry-vscode-gherkin-plugin-<version>.vsix`.

## [1.2.0] - 2026-02-16

### Added

- VS Code extension: real-time diagnostics for `.feature` files via Language Server Protocol.
- VS Code extension: 82 analysis rules (all except spelling-accuracy) with configurable severity.
- VS Code extension: default quality profile with 53 rules active out of the box.
- VS Code extension: per-rule configuration via VS Code settings.
- VS Code extension: cross-file analysis for unique names and consistent language.
- VS Code extension: TextMate syntax highlighting for Gherkin.
- `gherkin-lsp-server` Maven module wrapping the shared analyzer for LSP.
- **9 new analysis rules** (rules 75 - 83), bringing the total to **83 rules**:
  - `unique-examples-headers` (CRITICAL) - Examples table column headers must be unique.
  - `no-empty-examples-cells` (MAJOR) - Examples table data cells must not be empty.
  - `no-duplicate-scenario-bodies` (MAJOR) - Scenarios within the same scope must not have identical step sequences.
  - `no-conflicting-tags` (MAJOR) - Configurable mutually exclusive tag pair detection.
  - `no-commented-out-steps` (MINOR) - Detect dead step definitions in comments.
  - `background-step-count-limit` (MAJOR) - Background sections must not exceed configurable step count (default: 5).
  - `feature-name-matches-filename` (MINOR) - Feature names should correspond to file names.
  - `scenario-description-recommended` (INFO) - Scenarios should include descriptions.
  - `no-empty-doc-strings` (MINOR) - Doc strings must not be empty.

### Changed

- Quality profile updated: **53 rules active** out of 83 (was 50/74).

## [1.1.0] - 2026-02-14

### Added

- **26 new analysis rules** (rules 49 - 74), bringing the total to **74 rules**. The new rules are organized into six categories:
  - **Rule Block Quality** (49 - 52): `rule-name-required`, `rule-scenario-required`, `unique-rule-name`, `rule-description-recommended` - dedicated analysis for `Rule` keyword blocks (Gherkin 6+).
  - **Structural Integrity** (53 - 56): `outline-placeholder-required`, `scenario-outline-requires-examples`, `background-needs-multiple-scenarios`, `blank-line-before-scenario` - verify structural correctness of Scenario Outlines, Backgrounds, and formatting.
  - **Rule-Scoped Best Practices** (57 - 60): `rule-scenario-count-limit`, `feature-rule-count-limit`, `no-redundant-rule-tags`, `rule-tag-placement` - enforce quality standards within Rule blocks.
  - **Advanced Quality** (61 - 63): `examples-name-when-multiple`, `consistent-scenario-keyword`, `no-duplicate-tags` - detect subtle quality issues in Scenario Outlines, keyword usage, and tag management.
  - **Ecosystem Parity** (64 - 69): `no-multiple-empty-lines`, `required-tags`, `no-restricted-tags`, `name-max-length`, `one-space-between-tags`, `no-partially-commented-tag-lines` - align with popular Gherkin linters (gherkin-lint, Specflow.Gherkin.Net).
  - **Configurable Quality Thresholds** (70 - 74): `outline-single-example-row`, `no-restricted-patterns`, `max-tags-per-element`, `feature-file-max-lines`, `data-table-max-columns` - team-configurable limits for file size, tag count, data table width, and content patterns.
- **Quality profile updated**: 50 rules active out of 74 (was 38 out of 48).
- **Integration test suite** (`its/` module) for end-to-end verification against a running SonarQube instance.

### Fixed

- **`consistent-indentation`**: Complete rewrite to use AST-based visitor callbacks instead of hardcoded English keyword matching. Now supports all 70+ Gherkin languages and correct indentation levels for `Rule` block nesting.
- **`use-scenario-outline-for-examples`**: Uses language-aware keyword comparison via `GherkinDialectProvider` instead of English-only substring matching. No longer produces false positives on non-English Scenario Outlines.
- **`shared-given-to-background`**: Scoped analysis to each container (Feature or Rule) independently. Rule-level common Given steps are now correctly suggested for Rule-level Backgrounds rather than Feature-level.
- **`no-redundant-tags`**: Added `visitRule`/`leaveRule` tracking so scenario tags that duplicate Rule-level tags are now flagged as redundant.
- **`tag-placement`**: Added Rule-level tag consolidation pass. Tags common to all scenarios within a Rule (but not on the Rule itself) are now flagged for promotion to the Rule level.
- **`no-star-step-prefix` severity**: Reduced severity from MAJOR to MINOR and deactivated by default. The `*` step prefix is valid Gherkin syntax explicitly endorsed by the specification.
- **`no-star-step-prefix`**: Removed `"UNKNOWN".equals(step.keywordType())` condition so the rule only checks the literal keyword. Prevents duplicate issues with `no-unknown-step-type`.

### Changed

- `no-star-step-prefix` severity reduced from MAJOR to MINOR and deactivated by default (specification-endorsed syntax; teams can opt in).
- Scope separation for tag rules: `no-redundant-tags` (rule 37) handles Feature-level and Rule-level tag inheritance; `no-redundant-rule-tags` (rule 59) handles Rule-to-Scenario tag inheritance within Rule blocks. Similarly, `tag-placement` (rule 36) handles Feature-level consolidation; `rule-tag-placement` (rule 60) handles Rule-level consolidation.

## [1.0.0] - 2026-01-01

### Added

- Initial release of the Gherkin Analyzer plugin for SonarQube.
- **48 analysis rules** covering structure, design, style and convention, tags, variables and data, step patterns, comments and markers, spelling, and parser errors.
- **"Qualimetry Gherkin" quality profile** with 38 rules active by default.
- Uses the official Cucumber Gherkin parser (v28).
- Syntax highlighting and code metrics (NCLOC, comment lines, statements, functions, classes).
- Compatible with SonarQube Server 2025.1 LTA and later (Plugin API 13.x).
- Licensed under the Apache License, Version 2.0.
