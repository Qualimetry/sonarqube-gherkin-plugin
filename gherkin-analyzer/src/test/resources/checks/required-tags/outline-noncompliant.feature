Feature: Scenario Outline without tags

  # Noncompliant {{Add at least one tag matching the required pattern "smoke|regression" to this scenario.}}
  Scenario Outline: Parameterized login
    Given a user with name "<name>"
    When the user logs in
    Then the result is "<result>"

    Examples:
      | name  | result  |
      | alice | success |
      | bob   | failure |
