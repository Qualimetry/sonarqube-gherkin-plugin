Feature: Payment processing

  #TODO implement credit card validation
  # FIXME this scenario is flaky
  Scenario: Process payment
    Given a valid payment method
    When the customer confirms the payment
    Then the payment is processed successfully
