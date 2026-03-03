Feature: Order Management
  Manage customer orders throughout their lifecycle.

  Rule: Order placement
    Placing new orders for customers.

    Scenario: Place a standard order
      Given a customer with a valid account
      When they submit an order
      Then the order is confirmed

    Scenario: Place an express order
      Given a customer with a valid account
      When they submit an express order
      Then the order is confirmed with express shipping

  Rule: Order cancellation
    Cancelling existing orders.

    Scenario: Cancel a pending order
      Given an order in pending status
      When the customer cancels the order
      Then the order is marked as cancelled
