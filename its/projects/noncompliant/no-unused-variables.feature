Feature: Unused examples column
  Scenario Outline: Login attempt
    Given the user "<username>" exists
    When they log in
    Then they see the dashboard
    Examples:
      | username | unused_column |
      | alice    | extra_value   |
