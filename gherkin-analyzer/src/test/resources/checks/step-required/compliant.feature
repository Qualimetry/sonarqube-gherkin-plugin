Feature: Order tracking

  Background:
    Given the customer has a verified account

  Scenario: Customer tracks an active order
    Given the customer has placed an order
    When the customer views the order tracking page
    Then the current order status is displayed
