Feature: Order processing

  # This is a regular comment about the order flow
  # The system should handle concurrent requests
  Scenario: Process a new order
    Given a customer has items in the cart
    When the customer submits the order
    Then the order is created
