Feature: Payment processing

  @api-test
  Rule: Credit card payments

    # Noncompliant {{Remove this redundant tag 'api-test' that is already set at the Rule level.}}
    @api-test
    Scenario: Process Visa payment
      Given the customer has a Visa card
      When the customer pays with Visa
      Then the payment is processed

    # Noncompliant {{Remove this redundant tag 'api-test' that is already set at the Rule level.}}
    @api-test @regression
    Scenario: Process Mastercard payment
      Given the customer has a Mastercard
      When the customer pays with Mastercard
      Then the payment is processed
