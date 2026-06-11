Feature: E-commerce Platform
  Online shopping and order management capabilities.

  Rule: Cart management
    Customers can add and remove items from their cart.

    Scenario: Add item to cart
      Given the customer is browsing the catalog
      When the customer adds "Headphones" to the cart
      Then the cart should contain 1 item

  # Noncompliant {{Rename this Rule. The name "Cart management" is already used in this Feature.}}
  Rule: Cart management
    Scenario: Remove item from cart
      Given the customer has items in the cart
      When the customer removes "Headphones" from the cart
      Then the cart should be empty
