Feature: User session management

  Background:
    Given the user is on the login page
    When the user enters valid credentials

  Scenario: Access dashboard
    Given the user is authenticated
    When the user navigates to the dashboard
    Then the dashboard is displayed
