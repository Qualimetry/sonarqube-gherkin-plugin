Feature: Shopping Cart Checkout
  As a customer
  I want to checkout my cart
  So that I can complete my purchase

  Scenario: Complete checkout process
    Given I am logged in as a customer
    And I have items in my cart
    When I proceed to checkout
    Then I should see the checkout page
    # Noncompliant
    Given I am logged in as a customer
    When I enter my shipping address
    Then I should be able to proceed to payment
