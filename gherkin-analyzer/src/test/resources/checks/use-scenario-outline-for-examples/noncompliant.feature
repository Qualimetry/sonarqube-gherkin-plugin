Feature: Product Purchase
  As a customer
  I want to purchase products
  So that I can buy items I need

  # Noncompliant
  Scenario: Purchase products with different payment methods
    Given I am logged in as a customer
    And I have items in my cart
    When I checkout with payment method "<payment_method>"
    Then the order should be placed successfully

    Examples:
      | payment_method |
      | credit card    |
      | debit card     |
      | PayPal         |
