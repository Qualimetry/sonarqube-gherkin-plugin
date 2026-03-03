Feature: Duplicate examples headers test
  Scenario Outline: Login with duplicate headers
    Given the user "<username>" exists
    When they log in with "<username>"
    Then they see their dashboard
    Examples:
      | username | password | username |
      | alice    | pass123  | alice    |
