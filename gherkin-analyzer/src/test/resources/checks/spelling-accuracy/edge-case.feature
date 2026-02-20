Feature: Multi-currency transaction processing
  The platform supports transactions in multiple currencies
  with real-time exchange rate conversion.

  Scenario: Convert USD to EUR during checkout
    Given the customer's cart total is $150.00 USD
    And the current USD to EUR exchange rate is 0.92
    When the customer selects EUR as the payment currency
    Then the displayed total is 138.00 EUR
    And the exchange rate disclaimer is shown
