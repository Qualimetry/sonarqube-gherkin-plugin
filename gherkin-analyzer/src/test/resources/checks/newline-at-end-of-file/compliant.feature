Feature: Shipment tracking

  Scenario: Customer tracks a shipment
    Given the customer has placed an order
    And the order has been dispatched
    When the customer views the tracking page
    Then the current shipment status is displayed
    And the estimated delivery date is shown
