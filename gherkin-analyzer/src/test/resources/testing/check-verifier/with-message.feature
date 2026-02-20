Feature: Inventory management

  # Noncompliant {{Test issue on scenario: Check stock levels}}
  Scenario: Check stock levels
    Given the warehouse has products in stock
    When I request the inventory report
    Then I should see current stock quantities
