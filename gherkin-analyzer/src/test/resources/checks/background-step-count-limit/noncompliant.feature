Feature: Complex setup

  # Noncompliant {{This Background has 7 steps (maximum allowed: 5). Consider moving some steps into individual scenarios.}}
  Background:
    Given a registered user
    And the user is logged in
    And the user has a verified email
    And the user has a premium subscription
    And the user has enabled two-factor authentication
    And the user has accepted the latest terms
    And the user is on the home page

  Scenario: View settings
    When the user navigates to settings
    Then the settings page is displayed
