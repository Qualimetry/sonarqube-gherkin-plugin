Feature: User authentication

  Scenario: Successful login
    This scenario verifies that a user with valid credentials
    can successfully log in to the system.

    Given the user is on the login page
    When the user enters valid credentials
    Then the user is redirected to the dashboard

  Scenario Outline: Login with different roles
    This scenario tests login behaviour across different user roles
    to ensure proper access control.

    Given the user is on the login page
    When the user logs in as "<role>"
    Then the user sees the "<dashboard>" dashboard

    Examples:
      | role  | dashboard |
      | admin | admin     |
      | user  | user      |
