Feature: Regular tagging

  @smoke @regression
  Scenario: Normal tags
    Given a precondition
    When an action is taken
    Then the result is verified

  @wip @release-ready
  Scenario: More tags
    Given another precondition
    When another action is taken
    Then another result is verified
