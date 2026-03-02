Feature: E-commerce Platform
  Online shopping and order management capabilities.

  Rule: Cart management
    Customers can add and remove items from their cart.

    Scenario: Add item to cart
      Given the customer is browsing the catalog
      When the customer adds "Headphones" to the cart
      Then the cart should contain 1 item

  Rule: Checkout process
    Customers can complete their purchase through checkout.

    Scenario: Complete checkout
      Given the customer has items in the cart
      When the customer proceeds to checkout
      Then the order is placed successfully
