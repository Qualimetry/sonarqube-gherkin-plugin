@smoke @regression
Feature: No duplicate tags - compliant

  @critical
  Scenario: First scenario
    Given a step
    When something happens
    Then it works

  @fast @reliable
  Scenario: Second scenario
    Given another step
    When another thing happens
    Then it works too

  Scenario Outline: Parameterized scenario
    Given a user "<user>"
    When they log in
    Then they see "<page>"

    @positive
    Examples: Valid users
      | user  | page      |
      | admin | dashboard |

    @negative
    Examples: Invalid users
      | user    | page  |
      | unknown | error |
