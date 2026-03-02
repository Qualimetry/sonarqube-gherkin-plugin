Feature: User authentication

  Scenario: Successful login
    Given a registered user
    When the user logs in
    Then the dashboard is displayed

  Scenario: Failed login
    Given a registered user
    When the user enters wrong credentials
    Then an error message is shown
