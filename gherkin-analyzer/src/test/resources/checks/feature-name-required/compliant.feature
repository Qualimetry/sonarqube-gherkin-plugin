Feature: Shopping cart management
  Customers can add, update, and remove items from their shopping cart.

  Scenario: Add a product to the cart
    Given the customer is browsing the product catalog
    When the customer adds "Wireless Headphones" to the cart
    Then the cart should contain 1 item
