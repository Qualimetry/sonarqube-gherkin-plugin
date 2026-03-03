Feature: Order tracking

  # Noncompliant {{Add at least one step to this Scenario.}}
  Scenario: Customer tracks an active order

  Scenario: Customer views delivery estimate
    Given the customer has an order in transit
    When the customer checks the delivery estimate
    Then the estimated delivery date is displayed
