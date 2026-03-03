Feature: Consistent keywords - noncompliant

  Scenario: First scenario
    Given a step
    When something happens
    Then it works

  Scenario: Second scenario
    Given another step
    When another thing happens
    Then it works too

  # Noncompliant
  Example: Third scenario uses different keyword
    Given a step
    When something happens
    Then it works
