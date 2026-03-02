Feature: Simple checkout

  Scenario: Complete checkout
    Given items in the cart
    When the user proceeds to checkout
    Then the order is confirmed
