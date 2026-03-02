Feature: Payment processing

  Scenario: Process credit card payment
    Given the customer has items in the cart
    And the cart total is $75.00
    When the customer pays with a credit card
    Then the payment is authorised
    And a receipt is emailed to the customer

  Scenario: Process refund
    Given the customer has a completed order
    When the customer requests a refund
    Then the refund is processed within 5 business days
