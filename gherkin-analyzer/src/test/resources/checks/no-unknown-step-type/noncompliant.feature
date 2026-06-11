Feature: Order management

  Scenario: Place an order
    Given the customer has an active account
    # Noncompliant
    * the customer performs an action
    Then the order is placed successfully
