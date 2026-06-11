Feature: Order Management
  Manage customer orders throughout their lifecycle.

  Rule: Order placement
    Scenario: Place a standard order
      Given a customer with a valid account
      When they submit an order
      Then the order is confirmed

  Rule: Order cancellation
    Scenario: Cancel a pending order
      Given an order in pending status
      When the customer cancels the order
      Then the order is marked as cancelled

  Rule: Order tracking
    Scenario: Track an order
      Given a confirmed order
      When the customer checks the status
      Then the current status is displayed
