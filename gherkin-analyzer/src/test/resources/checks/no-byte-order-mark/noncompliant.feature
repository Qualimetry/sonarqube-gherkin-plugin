Feature: Warehouse inventory

  Scenario: Receive new stock
    Given a shipment of 100 units arrives
    When the warehouse clerk scans the items
    Then the inventory count increases by 100
