Feature: Quick order processing

  Scenario: Place a quick order
    # Noncompliant
    * the customer places a quick order
    Then the order confirmation is displayed

  Scenario: Standard order with star continuation
    Given the customer has an active account
    # Noncompliant
    * the customer has items in the cart
    When the customer checks out
    # Noncompliant
    * the customer selects express shipping
    Then the order is placed
