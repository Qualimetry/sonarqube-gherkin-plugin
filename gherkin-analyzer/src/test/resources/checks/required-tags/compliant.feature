Feature: User login

  @smoke
  Scenario: Successful login
    Given a registered user
    When the user logs in
    Then the dashboard is displayed

  @regression
  Scenario: Failed login
    Given a registered user
    When the user enters wrong credentials
    Then an error is shown
