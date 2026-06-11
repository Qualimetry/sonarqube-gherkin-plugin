Feature: Payment processing

  # This is a normal comment about the payment flow
  Scenario: Process a credit card payment
    Given the customer has items in the cart
    # Delivery window is based on postcode
    When the customer pays by credit card
    # The prefix mentioned here is the card scheme prefix
    Then the payment is processed

  Scenario: Process a bank transfer
    Given the customer has items in the cart
    # The suffix me-too flag is ignored by the gateway
    When the customer pays by bank transfer
    # fixmenot is the name of the retry queue
    Then the payment is pending confirmation
