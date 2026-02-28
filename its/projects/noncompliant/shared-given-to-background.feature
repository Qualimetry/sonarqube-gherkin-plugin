Feature: Shared given steps should be in background
  Scenario: First scenario
    Given the system is ready
    When the user does action A
    Then result A occurs

  Scenario: Second scenario
    Given the system is ready
    When the user does action B
    Then result B occurs
