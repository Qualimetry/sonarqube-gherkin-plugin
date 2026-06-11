Feature: Order processing

  Scenario: Submit an order with a long step
    Given the user has an order
    # Noncompliant {{Shorten this line (130 characters; maximum allowed is 120).}}
    When the user enters xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    Then the order is accepted
