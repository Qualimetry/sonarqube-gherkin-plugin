Feature: Scenario Outline with tags

  @smoke
  Scenario Outline: Parameterized login
    Given a user with name "<name>"
    When the user logs in
    Then the result is "<result>"

    Examples:
      | name  | result  |
      | alice | success |
      | bob   | failure |
