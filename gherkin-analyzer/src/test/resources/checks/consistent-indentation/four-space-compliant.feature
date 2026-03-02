Feature: Warehouse management
    Warehouse staff can track inventory across locations.

    Background:
        Given the warehouse system is online

    Scenario: Check stock levels
        Given the warehouse has 200 units of "Circuit Board A"
        When the manager requests the stock report
        Then the system shows 200 units available
