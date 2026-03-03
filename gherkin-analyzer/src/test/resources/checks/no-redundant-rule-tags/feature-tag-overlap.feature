@smoke
Feature: Payment processing with feature tag

  @smoke
  Rule: Credit card payments

    @smoke
    Scenario: Process Visa payment
      Given the customer has a Visa card
      When the customer pays with Visa
      Then the payment is processed
