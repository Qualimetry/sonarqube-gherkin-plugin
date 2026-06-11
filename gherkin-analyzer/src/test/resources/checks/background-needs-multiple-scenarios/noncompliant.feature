Feature: Order processing

  # Noncompliant {{This Background serves only one scenario. Inline the Background steps into the scenario for clarity.}}
  Background:
    Given a customer with an active account

  Scenario: Place an order
    When the customer adds an item to the cart
    Then the order is created
