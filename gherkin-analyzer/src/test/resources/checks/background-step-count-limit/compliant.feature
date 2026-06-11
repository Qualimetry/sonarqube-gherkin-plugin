Feature: User dashboard

  Background:
    Given a registered user
    And the user is logged in
    And the dashboard is loaded

  Scenario: View profile
    When the user clicks on profile
    Then the profile page is displayed
