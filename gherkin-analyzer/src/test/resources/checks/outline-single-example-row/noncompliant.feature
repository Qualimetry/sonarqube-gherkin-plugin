Feature: User login

  # Noncompliant {{This Scenario Outline has 1 Examples section(s), each with at most 1 data row(s). Consider using a plain Scenario instead.}}
  Scenario Outline: Login with single example
    Given the user navigates to the login page
    When the user enters "<username>" and "<password>"
    Then the user should see the dashboard

    Examples:
      | username | password |
      | admin    | secret   |
