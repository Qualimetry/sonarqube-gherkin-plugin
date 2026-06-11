Feature: Tagged scenarios with blank lines

  @smoke
  Scenario: First scenario with tag
    Given a simple setup
    When an action is performed
    Then the expected result occurs

  @regression @critical
  Scenario: Second scenario with tags
    Given another setup
    When another action is performed
    Then another result occurs
