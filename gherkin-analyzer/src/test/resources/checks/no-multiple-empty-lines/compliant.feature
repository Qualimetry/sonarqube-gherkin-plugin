Feature: Order management

  Scenario: Create order
    Given a customer account
    When the customer places an order
    Then the order is created

  Scenario: Cancel order
    Given an existing order
    When the customer cancels the order
    Then the order is cancelled
