Feature: Payment gateway integration

  # Noncompliant
  @credit-card
  Scenario: Process a Visa payment
    Given the customer has a Visa card on file
    When the customer submits a payment of $99.99
    Then the payment is authorized
