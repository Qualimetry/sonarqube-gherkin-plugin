Feature: Checkout process

  # Noncompliant
  Scenario: Complete checkout
    Given the customer has items in the cart
    When the customer proceeds to checkout
    Then the checkout page is displayed
    And the order summary shows the correct total
