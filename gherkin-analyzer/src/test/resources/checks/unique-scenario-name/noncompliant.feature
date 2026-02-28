Feature: Order Management
  As a customer
  I want to manage my orders
  So that I can track my purchases

  Scenario: View order details
    Given I have placed an order
    When I view the order details
    Then I should see order information

  Scenario: View order details
    Given I have multiple orders
    When I select an order
    Then I should see its details
