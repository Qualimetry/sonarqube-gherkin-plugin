Feature: Payment processing

  Scenario: Process a credit card payment
    Given the customer has items in the cart
    # Noncompliant
    # FIXME: this step fails intermittently
    When the customer pays by credit card
    # Noncompliant
    # FIX-ME: align with the new payment gateway
    Then the payment is processed

  Scenario: Process a bank transfer
    Given the customer has items in the cart
    # Noncompliant
    # fix_me check the transfer reference format
    When the customer pays by bank transfer
    # Noncompliant
    # Fix me: confirmation can take two days
    Then the payment is pending confirmation
