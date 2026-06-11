# Noncompliant:file {{Add a newline at the end of this file.}}
Feature: Shipment tracking

  Scenario: Customer tracks a shipment
    Given the customer has placed an order
    When the customer views the tracking page
    Then the current shipment status is displayed