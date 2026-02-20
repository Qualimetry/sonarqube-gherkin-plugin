@smoke @regression
Feature: User authentication

  @api @integration
  Scenario: User logs in with valid credentials
    Given the user navigates to the login page
    When the user enters valid credentials
    Then the user is authenticated successfully

  @smoke @api
  Scenario: Process payment transaction
    Given the customer has items in their cart
    When the customer completes checkout
    Then the payment is processed successfully
