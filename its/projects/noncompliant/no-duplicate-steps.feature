Feature: Shopping Cart Checkout

  Scenario: Complete checkout process
    Given I am logged in as a customer
    And I have items in my cart
    When I proceed to checkout
    Then I should see the checkout page
    Given I am logged in as a customer
    When I enter my shipping address
    Then I should be able to proceed to payment
