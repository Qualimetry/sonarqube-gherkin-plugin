Feature: Consistent keywords - compliant

  Scenario: First scenario
    Given a step
    When something happens
    Then it works

  Scenario: Second scenario
    Given another step
    When another thing happens
    Then it works too

  Scenario Outline: Parameterized scenario
    Given a user "<user>"
    When they log in
    Then they see "<page>"

    Examples:
      | user  | page      |
      | admin | dashboard |
