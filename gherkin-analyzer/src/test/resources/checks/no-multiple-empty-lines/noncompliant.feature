Feature: Order management

  Scenario: Create order
    Given a customer account
    When the customer places an order
    Then the order is created
# Noncompliant {{Remove this unnecessary blank line; only one consecutive blank line is allowed.}}


  Scenario: Cancel order
    Given an existing order
    When the customer cancels the order
    Then the order is cancelled
