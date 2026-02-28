@smoke @regression
Feature: User authentication

  # Noncompliant
  @smoke
  Scenario: User logs in
    Given the user navigates to login
    When the user enters credentials
    Then the user is authenticated

  # Noncompliant
  @regression
  Scenario: User logs out
    Given the user is logged in
    When the user clicks logout
    Then the user is logged out
