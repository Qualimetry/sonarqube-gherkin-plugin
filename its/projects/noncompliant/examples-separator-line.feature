Feature: Missing blank line before examples
  Scenario Outline: Login
    Given the user "<username>" exists
    When they log in with "<password>"
    Then they see the dashboard
    Examples:
      | username | password |
      | alice    | pass123  |
