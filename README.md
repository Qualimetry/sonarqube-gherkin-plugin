# Qualimetry Gherkin Analyzer - SonarQube Plugin

[![CI](https://github.com/Qualimetry/sonarqube-gherkin-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/Qualimetry/sonarqube-gherkin-plugin/actions/workflows/ci.yml)

**Author**: The [Qualimetry](https://qualimetry.com) team at SHAZAM Analytics Ltd

A SonarQube plugin that provides comprehensive static analysis of Cucumber Gherkin `.feature` files, powered by the same analysis engine as the [Qualimetry Gherkin Analyzer for VS Code](https://github.com/Qualimetry/vscode-gherkin-plugin). It brings the same rules into your quality gate so teams can identify and fix quality issues in CI/CD - catching structural problems, design anti-patterns, style inconsistencies, and correctness errors at the pipeline. The plugin uses the official Cucumber Gherkin parser and is licensed under the Apache License 2.0.

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

## Rules

The plugin provides **83 analysis rules** under the repository key `qualimetry-gherkin`, organized by severity:

| Severity | Count |
|---|---|
| CRITICAL | 11 |
| MAJOR | 30 |
| MINOR | 36 |
| INFO | 6 |

Rules are grouped into 17 categories:

| Category | Rule Numbers |
|---|---|
| Structure | 1 - 10, 80, 82 |
| Design | 11 - 21, 77 |
| Style and Convention | 22 - 33, 81 |
| Tag | 34 - 38, 78 |
| Variable and Data | 39, 75 - 76 |
| Step Pattern | 40 - 43 |
| Comment and Marker | 44 - 46, 79 |
| Spelling | 47 |
| Parser Error | 48 |
| Rule Block Quality | 49 - 52 |
| Structural Integrity | 53 - 56, 83 |
| Rule-Scoped Best Practices | 57 - 60 |
| Advanced Quality | 61 - 63 |
| Ecosystem Parity | 64 - 69 |
| Configurable Thresholds | 70 - 74 |

See [CHANGELOG.md](CHANGELOG.md) for full details on each release.

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
