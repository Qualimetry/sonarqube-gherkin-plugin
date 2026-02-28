Feature: Simple operations

  Scenario: Add items to cart
    Given the user is on the product page
    When the user adds an item to the cart
    Then the cart count is updated
