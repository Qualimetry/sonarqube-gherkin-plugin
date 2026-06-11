Feature: User login

  @ui
  # Noncompliant {{Add at least one tag matching the required pattern "smoke|regression" to this scenario.}}
  Scenario: Successful login
    Given a registered user
    When the user logs in
    Then the dashboard is displayed

  @smoke
  Scenario: Failed login
    Given a registered user
    When the user enters wrong credentials
    Then an error is shown
