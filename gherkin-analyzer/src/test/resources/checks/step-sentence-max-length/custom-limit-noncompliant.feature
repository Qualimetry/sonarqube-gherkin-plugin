Feature: Customer notifications

  Scenario: Send order confirmation
    Given a customer has placed an order
    # Noncompliant
    When the system processes the order and generates the confirmation
    Then the customer receives an email
