# Noncompliant {{Add a name to this Feature.}}
Feature:

  Scenario: Process a refund
    Given a customer has a completed order
    When the customer requests a refund
    Then the refund is processed within 5 business days
