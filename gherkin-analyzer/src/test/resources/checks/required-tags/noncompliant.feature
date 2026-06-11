Feature: User login

  # Noncompliant {{Add at least one tag matching the required pattern ".*" to this scenario.}}
  Scenario: Successful login
    Given a registered user
    When the user logs in
    Then the dashboard is displayed
