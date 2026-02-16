Feature: Payment processing

  Scenario: Process a credit card payment
    Given the customer has items in the cart
    # Noncompliant
    # FIXME: this step fails intermittently
    When the customer pays by credit card
    Then the payment is processed
