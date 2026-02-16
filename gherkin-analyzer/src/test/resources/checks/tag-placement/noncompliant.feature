# Noncompliant
Feature: Payment processing

  @smoke @regression
  Scenario: Process credit card payment
    Given the customer has items in cart
    When the customer completes checkout
    Then payment is processed successfully

  @smoke @regression
  Scenario: Process PayPal payment
    Given the customer has items in cart
    When the customer selects PayPal
    Then payment is processed successfully

  @smoke @regression
  Scenario: Process bank transfer
    Given the customer has items in cart
    When the customer selects bank transfer
    Then payment is processed successfully
