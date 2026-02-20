# Integration test projects (ITS)

- **compliant**: feature files that satisfy the plugin's rules; analysis should pass.
- **noncompliant**: feature files that violate rules (e.g. missing steps, no scenario); analysis should report issues and fail the quality gate.

Analyses are pushed with `scripts/push-its-to-staging-sonar.ps1` (default: UAT; pass `-SonarHostUrl` for staging or other). For **noncompliant** to show issues and fail on the server, the SonarQube project must use the **Qualimetry Gherkin** quality profile for the Gherkin language:

- **Quality Profiles** → select language **Gherkin** → set **Qualimetry Gherkin** as default, or  
- **Project** → **Quality Profiles** → for the project, set **Qualimetry Gherkin** for Gherkin.

If the project uses a profile with no Gherkin rules, no issues are raised and both projects appear to pass.
