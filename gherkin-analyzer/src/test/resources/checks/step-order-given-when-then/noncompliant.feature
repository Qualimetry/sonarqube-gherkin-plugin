Feature: Order management

  Scenario: Complete a purchase
    Given the customer has items in the shopping cart
    When the customer proceeds to checkout
    Then the order total is displayed
    # Noncompliant {{Unexpected Given step. Reorder the steps of this scenario to follow Given/When/Then order.}}
    Given the customer enters a shipping address
