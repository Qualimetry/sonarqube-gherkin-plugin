Feature: Examples with no data rows
  Scenario Outline: Login attempt
    Given the user "<username>" exists
    When they log in with password "<password>"
    Then they should see the dashboard
    Examples:
      | username | password |
