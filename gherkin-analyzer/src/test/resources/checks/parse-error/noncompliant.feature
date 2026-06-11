Feature: User login

  Scenario: Login with valid credentials
    Given the user has a valid account
    When the user logs in
    Then the user sees the dashboard
