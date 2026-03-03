Feature: Order processing

  Background:
    Given a customer with an active account

  Scenario: Place an order
    When the customer adds an item to the cart
    Then the order is created

  Scenario: Cancel an order
    Given an existing order
    When the customer cancels the order
    Then the order status is cancelled
