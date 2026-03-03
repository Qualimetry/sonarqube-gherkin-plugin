Feature: Payment processing

  @api-test
  Rule: Credit card payments

    @api-test
    Scenario: Process Visa payment
      Given the customer has a Visa card
      When the customer pays with Visa
      Then the payment is processed

    Scenario: Process Mastercard payment
      Given the customer has a Mastercard
      When the customer pays with Mastercard
      Then the payment is processed
