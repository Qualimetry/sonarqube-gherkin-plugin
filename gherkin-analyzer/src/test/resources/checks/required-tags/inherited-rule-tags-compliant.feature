Feature: User login

  @smoke
  Scenario: Successful login
    Given a registered user
    When the user logs in
    Then the dashboard is displayed

  @regression
  Rule: Locked accounts

    Scenario: Login with a locked account
      Given a locked account
      When the user logs in
      Then an error is shown
