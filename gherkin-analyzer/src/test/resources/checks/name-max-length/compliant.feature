Feature: User authentication

  Scenario: Successful login redirects to dashboard
    Given a registered user
    When the user logs in
    Then the dashboard is displayed

  Scenario: Failed login shows error
    Given a registered user
    When the user enters wrong credentials
    Then an error is shown
