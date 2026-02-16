Feature: Order management

  Scenario: Place an order
    Given the customer has an active account
    # Noncompliant
    * the customer has items in the cart
    When the customer clicks checkout
    # Noncompliant
    * the customer confirms the order
    Then the order is placed successfully
