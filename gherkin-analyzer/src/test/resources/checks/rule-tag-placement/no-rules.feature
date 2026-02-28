Feature: Simple Feature Without Rules
  A feature that does not use the Rule keyword.

  @smoke
  Scenario: First scenario
    Given a simple setup
    When an action is performed
    Then the expected result occurs

  @smoke
  Scenario: Second scenario
    Given another setup
    When another action is performed
    Then another result occurs
