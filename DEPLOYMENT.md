# Deployment Guide: Gherkin Analyzer Plugin for SonarQube

This guide covers how to build, release, and deploy the Gherkin Analyzer plugin. This repository contains the plugin source; build and release from here.

## Prerequisites

| Requirement | Version |
|---|---|
| JDK | 17, 21, or 25 |
| Maven | 3.6.1+ |
| Git | 2.x+ |
| SonarQube Server (target) | 2025.1 LTA or later |

---

## 1. Build the Release JAR

From the repository root:

```bash
mvn clean package
```

> **Windows note**: If your default JDK is not 17+, set `JAVA_HOME` first:
> ```powershell
> $env:JAVA_HOME = "C:\jdk17"; mvn clean package
> ```

The plugin JAR is produced at:

```
gherkin-plugin/target/qualimetry-gherkin-plugin-1.2.0.jar
```

To also run the full test suite (recommended before any release):

```bash
mvn clean verify
```

All tests must pass.

---

## 2. Version Management

The version is defined in three POM files that must stay in sync:

| File | Element |
|---|---|
| `pom.xml` | `<version>` (parent) |
| `gherkin-analyzer/pom.xml` | `<parent><version>` |
| `gherkin-plugin/pom.xml` | `<parent><version>` |

### Bumping the version

Update all three files. For example, to go from `1.2.0` to `1.3.0`:

```bash
mvn versions:set -DnewVersion=1.3.0 -DgenerateBackupPoms=false
```

This updates all three POMs automatically. After bumping, update the JAR filename references in `README.md` and `CHANGELOG.md`.

---

## 2.5 Source control and remotes

- **Azure DevOps** (remote `origin`) is the canonical repository; full development history stays here.
- **GitHub** (remote `github`) is a public snapshot. Do not push to it with `git push github main`; that would publish full history. Use the publish script instead.

**First-time setup** (from repository root):

```bash
git remote add github https://github.com/Qualimetry/sonarqube-gherkin-plugin.git
```

**Daily workflow:** Commit locally, then `git push origin main` to push to Azure.

**Publish to GitHub:** Run `.\publish-to-github.ps1` from the repository root. The script creates a single-commit snapshot (excluding Azure-only paths such as `docs/`) and force-pushes to `github main`. GitHub Actions CI runs after each publish. Add any paths that must never appear on GitHub to the script's `$ExcludeFromGitHub` list.

---

## 3. Pushing Changes and CI

When you push to the `main` branch, GitHub Actions runs the CI workflow: it builds and runs tests on Java 17, 21, and 25, and uploads the plugin JAR as a build artifact. No release is created unless you use a release commit (see below).

---

## 4. Create a Release

### Option A: Automated via GitHub Actions

The CI workflow at `.github/workflows/ci.yml` provides:

- **On every push/PR**: builds and tests on Java 17, 21, and 25, uploads the JAR as a build artifact.
- **Automatic GitHub Release**: when a commit message starts with `release:`, the workflow creates a tagged GitHub Release with the JAR attached.

To trigger an automated release:

```bash
# 1. Bump version if needed
mvn versions:set -DnewVersion=1.2.0 -DgenerateBackupPoms=false

# 2. Update README.md and CHANGELOG.md for the new version.

# 3. Commit with the release: prefix
git add -A
git commit -m "release: 1.2.0"
git push origin main
```

The workflow will:
1. Build and test on Java 17, 21, and 25.
2. Create a Git tag `v1.2.0`.
3. Create a GitHub Release with the JAR attached and release notes from `CHANGELOG.md`.

### Option B: Manual via GitHub UI

1. Build with `mvn clean package`.
2. Go to your GitHub repository > **Releases** > **Draft a new release**.
3. Create tag `v1.2.0`, set title to `Gherkin Analyzer Plugin 1.2.0`.
4. Add release notes (see template below or copy from `CHANGELOG.md`).
5. Attach the JAR: `gherkin-plugin/target/qualimetry-gherkin-plugin-1.2.0.jar`.
6. Click **Publish release**.

### Option C: Manual via GitHub CLI

```bash
# From the repository root after building:
gh release create v1.2.0 \
  gherkin-plugin/target/qualimetry-gherkin-plugin-1.2.0.jar \
  --repo Qualimetry/sonarqube-gherkin-plugin \
  --title "Gherkin Analyzer Plugin 1.2.0" \
  --notes-file RELEASE_NOTES.md
```

### Release notes template

Use the relevant section from `CHANGELOG.md`, or see the template in Section 6.

---

## 5. Deploy to SonarQube Server

### Step 1: Download the JAR

Download `qualimetry-gherkin-plugin-1.2.0.jar` from:
- The GitHub Release assets, **or**
- Build it locally with `mvn clean package`.

### Step 2: Install the plugin

Copy the JAR to the SonarQube extensions directory:

```bash
# Linux / macOS
cp qualimetry-gherkin-plugin-1.2.0.jar /opt/sonarqube/extensions/plugins/

# Windows
copy qualimetry-gherkin-plugin-1.2.0.jar C:\sonarqube\extensions\plugins\
```

> **Important**: Only one version of the plugin should be present in `extensions/plugins/`. Remove any older `qualimetry-gherkin-plugin-*.jar` files before copying the new one.

### Step 3: Restart SonarQube

```bash
# Linux / macOS (systemd)
sudo systemctl restart sonarqube

# Linux / macOS (manual)
/opt/sonarqube/bin/linux-x86-64/sonar.sh restart

# Windows (service)
net stop SonarQube && net start SonarQube

# Docker
docker restart sonarqube
```

### Step 4: Verify the installation

1. Log in to SonarQube.
2. Go to **Administration > Marketplace > Installed**.
3. Confirm **Gherkin Analyzer** appears with the installed version and organization **SHAZAM Analytics Ltd**.
4. Go to **Rules** and filter by repository **Qualimetry Gherkin** — 83 rules should be listed.
5. Go to **Quality Profiles** and confirm the **Qualimetry Gherkin** profile exists with 53 active rules.

### Step 5: Scan a project

Add the Gherkin language to your project's analysis. In `sonar-project.properties`:

```properties
sonar.sources=src
sonar.language=gherkin
```

Or for a multi-language project, just ensure `.feature` files are under `sonar.sources`. The plugin automatically picks up all `.feature` files.

Run the scanner:

```bash
sonar-scanner
```

---

## 6. Upgrading the Plugin

1. Download the new version JAR.
2. Remove the old JAR from `extensions/plugins/`.
3. Copy the new JAR to `extensions/plugins/`.
4. Restart SonarQube Server.
5. Verify the new version in **Administration > Marketplace > Installed**.

Quality profile customizations are preserved. New rules added in future versions are not automatically activated — review and activate them in your quality profile if desired.

---

## 7. Uninstalling

1. Remove `qualimetry-gherkin-plugin-*.jar` from `extensions/plugins/`.
2. Restart SonarQube Server.

> **Note**: Existing analysis data (issues, metrics) for the `gherkin` language will remain in the SonarQube database but will no longer be updated.

---

## Troubleshooting

| Problem | Solution |
|---|---|
| Plugin does not appear after restart | Check SonarQube logs (`logs/web.log`) for errors. Ensure the JAR is in the correct directory and no duplicate plugin JARs exist. |
| "Incompatible plugin API" error | The plugin requires SonarQube 2025.1+. It is not compatible with SonarQube 9.x or 10.x. |
| `.feature` files not analyzed | Ensure files have the `.feature` extension and are under the directory specified by `sonar.sources`. |
| No quality profile available | The "Qualimetry Gherkin" profile should be created automatically. If missing, check that the plugin loaded without errors in `logs/web.log`. |
| Build fails with "invalid target release: 17" | Your JDK is older than 17. Set `JAVA_HOME` to a JDK 17+ installation. |
