Feature: Warehouse inventory
  Track inventory levels across multiple warehouses.

  Scenario: Check stock level
    Given the warehouse has 100 units of "Widget A"
    When the inventory manager checks the stock level
    Then the system reports 100 units available

  Scenario: Receive new shipment
    Given a shipment of 50 units of "Widget A" arrives
    When the warehouse clerk processes the shipment
    Then the stock level increases by 50 units
