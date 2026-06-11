Feature: Inventory management

  Scenario: Receive new stock
    Given the warehouse has 100 units of "Widget A"
    When a shipment of 50 units arrives
    Then the stock level should be 150 units
