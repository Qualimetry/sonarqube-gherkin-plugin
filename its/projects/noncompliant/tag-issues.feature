@smoke @regression
Feature: User authentication

  @smoke
  Scenario: User logs in with valid credentials
    Given the user navigates to login
    When the user enters valid credentials
    Then the user is authenticated

  @regression
  @INVALID_TAG_PATTERN
  @smoke @smoke
  Scenario: User logs out successfully
    Given the user is logged in
    When the user clicks the logout button
    Then the user is logged out
