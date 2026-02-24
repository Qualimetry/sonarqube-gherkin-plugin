# Noncompliant {{Feature name "Order cancellation policy" does not correspond to file name "noncompliant.feature".}}
Feature: Order cancellation policy

  Scenario: Cancel an order
    Given a pending order exists
    When the customer requests cancellation
    Then the order is cancelled
