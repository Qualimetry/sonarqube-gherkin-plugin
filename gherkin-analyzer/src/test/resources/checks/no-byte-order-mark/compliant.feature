Feature: Warehouse inventory

  Scenario: Receive new stock
    Given a shipment of 100 units arrives at the warehouse
    And the items are inspected for damage
    When the warehouse clerk scans each item into the system
    Then the inventory count increases by 100
    And the receiving report is filed

  Scenario: Dispatch stock for an order
    Given an order requires 25 units of product A
    When the picker retrieves the items
    Then the inventory count decreases by 25
