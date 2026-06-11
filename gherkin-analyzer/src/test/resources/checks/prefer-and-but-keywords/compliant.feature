Feature: Shopping cart

  Scenario: Add multiple items to cart
    Given the customer has an active session
    And the customer has an empty cart
    When the customer adds "Widget" to the cart
    And the customer adds "Gadget" to the cart
    Then the cart contains 2 items
    And the cart total reflects both items

  Scenario: Remove an item from cart
    Given the customer has a cart with 3 items
    But the customer no longer wants "Gadget"
    When the customer removes "Gadget" from the cart
    Then the cart contains 2 items
