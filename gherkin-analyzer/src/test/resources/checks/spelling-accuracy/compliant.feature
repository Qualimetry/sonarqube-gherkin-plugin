Feature: User authentication

  Scenario: Login with valid credentials
    Given the user has a registered account
    When the user logs in with valid credentials
    Then the user is authenticated

  Scenario: Login with invalid credentials
    Given the user has a registered account
    When the user logs in with invalid credentials
    Then the login attempt is rejected
