Feature: Payment processing

  Rule: Credit card payments

    Scenario: Process a credit card payment
      Given a customer with a valid credit card
      When the customer pays by credit card
      Then the payment is processed

    # Noncompliant {{This scenario has an identical step sequence to the scenario at line 5. Consider consolidating.}}
    Scenario: Duplicate credit card payment
      Given a customer with a valid credit card
      When the customer pays by credit card
      Then the payment is processed
