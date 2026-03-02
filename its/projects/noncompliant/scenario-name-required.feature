Feature: Order processing

  Scenario:
    Given a customer has an account
    When the customer places an order
    Then the order is confirmed
