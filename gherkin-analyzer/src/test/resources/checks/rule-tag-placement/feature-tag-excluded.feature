@smoke
Feature: Payment processing with feature tag

  Rule: Credit card payments

    @smoke
    Scenario: Process Visa payment
      Given the customer has a Visa card
      When the customer pays with Visa
      Then the payment is processed

    @smoke
    Scenario: Process Mastercard payment
      Given the customer has a Mastercard
      When the customer pays with Mastercard
      Then the payment is processed
