Feature: Custom limit test

  # Noncompliant {{This Background has 4 steps (maximum allowed: 3). Consider moving some steps into individual scenarios.}}
  Background:
    Given a registered user
    And the user is logged in
    And the user has a verified email
    And the dashboard is loaded

  Scenario: View notifications
    When the user checks notifications
    Then notifications are displayed
