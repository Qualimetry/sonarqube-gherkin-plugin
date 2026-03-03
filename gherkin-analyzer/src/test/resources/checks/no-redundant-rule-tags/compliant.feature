Feature: Payment processing

  @api-test
  Rule: Credit card payments

    Scenario: Process Visa payment
      Given the customer has a Visa card
      When the customer pays with Visa
      Then the payment is processed

    @regression
    Scenario: Process Mastercard payment
      Given the customer has a Mastercard
      When the customer pays with Mastercard
      Then the payment is processed

  @integration
  Rule: Bank transfers

    @slow
    Scenario: Transfer between accounts
      Given two valid bank accounts
      When a transfer is initiated
      Then funds are moved successfully
