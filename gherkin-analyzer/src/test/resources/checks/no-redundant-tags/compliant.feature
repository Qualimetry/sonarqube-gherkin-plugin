@smoke @regression
Feature: User authentication

  @api-test
  Scenario: User logs in
    Given the user navigates to login
    When the user enters credentials
    Then the user is authenticated

  @integration-test
  Scenario: User logs out
    Given the user is logged in
    When the user clicks logout
    Then the user is logged out
