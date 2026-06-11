Feature: Payment processing

  # This is a normal comment about the payment flow
  Scenario: Process a credit card payment
    Given the customer has items in the cart
    # Delivery window is based on postcode
    When the customer pays by credit card
    # Refer to documentation for the card scheme rules
    Then the payment is processed

  Scenario: Process a bank transfer
    Given the customer has items in the cart
    # There are things to document about the gateway
    When the customer pays by bank transfer
    # The todos collection name is reserved by the API
    Then the payment is pending confirmation
