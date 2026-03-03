Feature: Order management

  # Noncompliant
  Scenario: Place an order
    Given I am a logged-in customer
    When I add a product to my cart
    Then I should see the product in my cart

  # Noncompliant
  Scenario: Cancel an order
    Given I have an existing order
    When I cancel the order
    Then the order status should be cancelled
