Feature: Payment processing

  # Noncompliant {{Remove the restricted tag "@skip"; it should not be committed.}}
  @skip
  Scenario: Process credit card payment
    Given a valid credit card
    When the payment is submitted
    Then the payment is approved

  @smoke
  Scenario: Process refund
    Given a completed payment
    When a refund is requested
    Then the refund is processed
