# Qualimetry Gherkin Analyzer - SonarQube Plugin

[![CI](https://github.com/Qualimetry/sonarqube-gherkin-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/Qualimetry/sonarqube-gherkin-plugin/actions/workflows/ci.yml)

A SonarQube plugin that provides comprehensive static analysis of Cucumber Gherkin `.feature` files in your CI/CD quality gate.

Powered by the same analysis engine as the [Qualimetry Gherkin Analyzer for VS Code](https://github.com/Qualimetry/vscode-gherkin-plugin) and the [Qualimetry Gherkin Analyzer for IntelliJ](https://github.com/Qualimetry/intellij-gherkin-plugin).

## Features

- **83 analysis rules** covering structure, design, style, tags, variables, spelling, and more.
- **Default quality profile** — 53 rules active out of the box, with 30 opt-in rules for team-specific conventions.
- **Customizable** — enable/disable rules, change severity, and set rule parameters per quality profile.
- **SonarCloud compatible** — works with both SonarQube Server and SonarCloud.

## Rule categories

| Category | Examples |
|----------|----------|
| Structure | Feature/scenario/step required, naming conventions |
| Design | Step ordering, single When, background best practices |
| Style & Convention | Indentation, trailing whitespace, line endings, spelling |
| Tags | Naming patterns, placement, duplicates, restrictions |
| Variables & Data | Unused variables, Examples column coverage |
| Step Patterns | Given/When/Then patterns, unknown step types |
| Comments & Markers | TODO, FIXME, comment patterns |
| Structural Integrity | Outline placeholders, Examples required, blank lines |
| Configurable Thresholds | Max scenarios, max steps, max tags, max lines |

## Compatibility

| SonarQube Server Version | Compatible? | Notes |
|---|---|---|
| 9.9 LTA | No | Plugin API too old. |
| 10.x | No | Plugin API too old. |
| 2025.1 LTA | Yes | Minimum supported version (API 11.1.0). |
| 2025.2+ | Yes | |
| 2026.1 LTA | Yes | Current LTA release. |
| Future releases | Yes | Forward-compatible via SonarQube's API stability guarantees. |

Also compatible with **SonarCloud**.

## Installation

1. Copy `qualimetry-gherkin-plugin-<version>.jar` to the `extensions/plugins/` directory of your SonarQube Server installation.
2. Restart SonarQube Server.
3. The "Qualimetry Gherkin" quality profile is automatically created as the default profile for the `gherkin` language.

## Quality Profile

The built-in "Qualimetry Gherkin" quality profile activates **53 rules out of 83** by default. The remaining 30 rules are opt-in and can be activated per project based on team conventions. You can customize the profile (enable/disable rules, change severity, set rule parameters) in SonarQube under **Quality Profiles**.

## Also available

The same analysis engine powers editor extensions for real-time feedback:

- **[VS Code extension](https://github.com/Qualimetry/vscode-gherkin-plugin)** — catch issues as you type, before you commit.
- **[IntelliJ plugin](https://github.com/Qualimetry/intellij-gherkin-plugin)** — real-time analysis in JetBrains IDEs and Qodana CI/CD.

Rule keys and severities align across all three tools so findings are directly comparable.

## Building from Source

Requires JDK 17+ and Maven 3.6.1+.

```bash
mvn clean package
```

The packaged plugin JAR is produced at `gherkin-plugin/target/qualimetry-gherkin-plugin-<version>.jar`.

To run the full test suite with verification:

```bash
mvn clean verify
```

### Build Requirements

| Requirement | Version |
|---|---|
| JDK (to compile) | 17+ |
| Maven | 3.6.1+ |
| Operating system | Any (Windows, macOS, Linux) |

## CI and Releases

The [CI](https://github.com/Qualimetry/sonarqube-gherkin-plugin/actions/workflows/ci.yml) workflow runs on every push and pull request to `main`: it builds and runs tests on Java 17, and uploads the plugin JAR as an artifact. A **GitHub Release** (tag + release notes + JAR) is created **only when a commit message starts with `release:`** (e.g. `release: 1.2.0`). For other pushes, only the build and tests run; no release is created.

## Contributing

Issues and feature requests are welcome. This project does not accept pull requests, commits, or other code contributions from third parties; the repository is maintained by the Qualimetry team only.

## License

This plugin is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

Copyright 2026 SHAZAM Analytics Ltd
