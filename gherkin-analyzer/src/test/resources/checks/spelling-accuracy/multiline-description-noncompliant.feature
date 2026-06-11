# Noncompliant@+3
Feature: Payment processing
  Payments are processed in real time.
  Refunds are procesed within five days.

  Scenario: Process a payment
    Given the customer has a valid card
    When the customer pays
    Then the payment is recorded
