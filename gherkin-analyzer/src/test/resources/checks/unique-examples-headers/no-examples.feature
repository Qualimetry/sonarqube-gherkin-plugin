Feature: Simple login

  Scenario: Successful login
    Given the user is on the login page
    When the user enters valid credentials
    Then the user is logged in
