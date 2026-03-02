Feature: Shopping cart

  Scenario: Add multiple items to cart
    Given the customer has an active session
    # Noncompliant
    Given the customer has an empty cart
    When the customer adds "Widget" to the cart
    # Noncompliant
    When the customer adds "Gadget" to the cart
    Then the cart contains 2 items
    # Noncompliant
    Then the cart total reflects both items
