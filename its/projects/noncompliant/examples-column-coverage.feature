Feature: Examples column not covering all placeholders
  Scenario Outline: User registration
    Given a user named "<username>"
    When they register with email "<missing_var>"
    Then the account is created
    Examples:
      | username |
      | alice    |
