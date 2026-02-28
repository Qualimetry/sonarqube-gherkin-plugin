Feature: Feature development

  @wip @release-ready
  # Noncompliant {{Tags "@wip" and "@release-ready" conflict and should not appear together.}}
  Scenario: Work in progress feature
    Given a new feature is being developed
    When the feature is partially implemented
    Then the tests should indicate incomplete work
