Feature: Multi-step workflow

  Scenario: Complex checkout
    Given the customer has items in the cart
    When the customer enters shipping details
    When the customer enters payment details
    Then the order is confirmed
