Feature: Payment processing

  # Noncompliant {{This message does not match the actual issue}}
  Scenario: Process a credit card payment
    Given I have items in my shopping cart
    When I enter valid payment details
    Then the payment should be processed
