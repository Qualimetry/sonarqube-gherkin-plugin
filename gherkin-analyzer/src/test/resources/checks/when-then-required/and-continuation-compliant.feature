Feature: Checkout completion

  Scenario: Outcome continued with And
    Given the cart has one item
    When the customer checks out
    Then a confirmation is shown
    And the cart is emptied

  Scenario: Action and outcome continued with And and But
    Given the customer is logged in
    And the cart has two items
    When the customer checks out
    And the customer confirms payment
    Then the order is created
    But no duplicate order exists
