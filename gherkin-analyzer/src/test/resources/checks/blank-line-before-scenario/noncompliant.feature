Feature: Order processing

  Scenario: Place an order
    Given a customer with an account
    When the customer adds an item to the cart
    Then the order is created
  # Noncompliant {{Add a blank line before this Scenario.}}
  Scenario: Cancel an order
    Given an existing order
    When the customer cancels the order
    Then the order status is cancelled
