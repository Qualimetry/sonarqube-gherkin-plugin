Feature: Tagged scenarios without blank lines

  Scenario: First scenario
    Given a simple setup
    When an action is performed
    Then the expected result occurs
  @smoke
  # Noncompliant {{Add a blank line before this Scenario.}}
  Scenario: Second scenario missing blank line before tag
    Given another setup
    When another action is performed
    Then another result occurs
