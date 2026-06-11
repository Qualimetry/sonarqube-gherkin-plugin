Feature: Payment processing

  Scenario: Process a credit card payment
    Given the customer has items in the cart
    # Noncompliant
    # TODO: implement this step
    When the customer pays by credit card
    # Noncompliant
    # To-Do: cover declined cards
    Then the payment is processed

  Scenario: Process a bank transfer
    Given the customer has items in the cart
    # Noncompliant
    # to_do verify the transfer reference
    When the customer pays by bank transfer
    # Noncompliant
    # still to do: confirmation flow
    Then the payment is pending confirmation
