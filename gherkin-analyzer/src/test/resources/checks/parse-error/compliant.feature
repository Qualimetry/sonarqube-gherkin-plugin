Feature: User login

  Scenario: Login with valid credentials
    Given the user has a valid account
    When the user logs in
    Then the user sees the dashboard

  Scenario: Login with expired account
    Given the user has an expired account
    When the user attempts to log in
    Then the user sees an expiration message
