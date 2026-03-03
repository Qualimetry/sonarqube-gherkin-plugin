Feature: Payment processing

  # Noncompliant {{Remove the restricted tag "@wip"; it should not be committed.}}
  @wip
  Scenario: Process credit card payment
    Given a valid credit card
    When the payment is submitted
    Then the payment is approved

  # Noncompliant {{Remove the restricted tag "@debug"; it should not be committed.}}
  @debug @smoke
  Scenario: Process refund
    Given a completed payment
    When a refund is requested
    Then the refund is processed
