Feature: Order management

  Scenario: Place an order
    Given the customer has an active account
    And the customer has items in the cart
    When the customer clicks checkout
    And the customer confirms the order
    Then the order is placed successfully
    And a confirmation email is sent
