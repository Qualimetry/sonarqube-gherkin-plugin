Feature: Payment processing

  @smoke
  Scenario: Process credit card payment
    Given a valid credit card
    When the payment is submitted
    Then the payment is approved

  @regression
  Scenario: Process refund
    Given a completed payment
    When a refund is requested
    Then the refund is processed
