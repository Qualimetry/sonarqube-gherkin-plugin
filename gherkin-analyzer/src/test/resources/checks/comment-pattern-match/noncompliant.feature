Feature: Payment processing

  Scenario: Process a refund
    Given the customer has a completed order
    # Noncompliant
    # HACK: workaround for the payment gateway
    When the customer requests a refund
    Then the refund is processed
