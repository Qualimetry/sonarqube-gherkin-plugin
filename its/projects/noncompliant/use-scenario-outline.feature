Feature: Plain scenario with examples table
  Scenario: Login with credentials
    Given the user "alice" exists
    When they log in with "pass123"
    Then they see the dashboard
    Examples:
      | username | password |
      | alice    | pass123  |
