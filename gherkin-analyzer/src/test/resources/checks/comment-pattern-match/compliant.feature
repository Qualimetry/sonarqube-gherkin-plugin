Feature: Payment processing

  # This is a normal comment
  Scenario: Process a refund
    Given the customer has a completed order
    # Standard refund flow
    When the customer requests a refund
    Then the refund is processed

  Scenario: Process a partial refund
    Given the customer has a completed order
    When the customer requests a partial refund
    Then the partial refund is processed
