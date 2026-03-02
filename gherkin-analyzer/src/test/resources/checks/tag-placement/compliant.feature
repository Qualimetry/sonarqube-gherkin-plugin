@smoke @regression
Feature: Payment processing

  Scenario: Process credit card payment
    Given the customer has items in cart
    When the customer completes checkout
    Then payment is processed successfully

  Scenario: Process PayPal payment
    Given the customer has items in cart
    When the customer selects PayPal
    Then payment is processed successfully

  @api-test
  Scenario: Process bank transfer
    Given the customer has items in cart
    When the customer selects bank transfer
    Then payment is processed successfully
