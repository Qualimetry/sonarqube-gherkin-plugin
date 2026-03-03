Feature: Payment processing

  # This is a normal comment about the payment flow
  Scenario: Process a credit card payment
    Given the customer has items in the cart
    # Delivery window is based on postcode
    When the customer pays by credit card
    Then the payment is processed

  Scenario: Process a bank transfer
    Given the customer has items in the cart
    When the customer pays by bank transfer
    Then the payment is pending confirmation
