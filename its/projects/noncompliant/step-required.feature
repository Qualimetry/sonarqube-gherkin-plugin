Feature: Inventory management

  Scenario: Check stock levels

  Scenario: Reorder products
    Given the stock level is below threshold
    When the reorder is triggered
    Then new stock is ordered from the supplier
