@Smoke
@RegressionTest
Feature: Payment gateway integration

  @CreditCard
  Scenario: Process a Visa payment
    Given the customer has a Visa card on file
    When the customer submits a payment of $99.99
    Then the payment is authorized

  @BankTransfer
  Scenario: Process a bank transfer
    Given the customer has linked a bank account
    When the customer initiates a transfer of $500
    Then the transfer is queued for processing
