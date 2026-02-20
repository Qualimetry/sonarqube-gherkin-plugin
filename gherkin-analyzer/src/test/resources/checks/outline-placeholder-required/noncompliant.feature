Feature: User login

  # Noncompliant {{This Scenario Outline does not reference any <placeholder> variables. Add placeholders to parameterize the steps, or use a plain Scenario instead.}}
  Scenario Outline: Successful login
    Given a registered user
    When the user logs in with valid credentials
    Then the user sees the dashboard

    Examples:
      | username | password |
      | alice    | secret1  |
      | bob      | secret2  |
