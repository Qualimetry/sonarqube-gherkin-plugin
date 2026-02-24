Feature: Order management

  Scenario: Complete a purchase
    Given the customer has items in the shopping cart
    When the customer proceeds to checkout
    Then the order total is displayed
    Given the customer enters a shipping address
