Feature: Payment processing

  # Noncompliant
  Scenario: Process credit card payment   
    Given the customer has items in the cart
    # Noncompliant
    When the customer pays with a credit card  
    Then the payment is authorised
