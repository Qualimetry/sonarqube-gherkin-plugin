@authentication
Feature: User authentication
  The system supports secure login and logout for registered users.

  Scenario: Successful login with valid credentials
    Successful login when the user provides valid credentials.
    Given the user is on the login page
    When the user enters a valid email and password
    Then the user is redirected to the dashboard
    And a welcome message is displayed

  Scenario: Failed login with invalid password
    Error is shown when the password is wrong.
    Given the user is on the login page
    When the user enters a valid email but an incorrect password
    Then an error message is displayed
    And the user remains on the login page

  Scenario: Logout from the application
    User can end the session via logout.
    Given the user is logged in
    When the user clicks the logout button
    Then the user is redirected to the login page
    And the session is terminated

  Scenario Outline: Login with different user roles
    Each role is redirected to the correct landing page after login.
    Given a user with role "<role>"
    When the user logs in with valid credentials
    Then the user is redirected to the "<landing_page>" page

    Examples:
      | role    | landing_page |
      | admin   | admin-panel  |
      | manager | reports      |
      | analyst | dashboard    |
