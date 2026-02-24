Feature: Order management

  Scenario: Place an order
    Given the customer has an active account
    And the customer has items in the cart
    When the customer clicks checkout
    And the customer confirms the order
    Then the order is placed successfully
    And a confirmation email is sent

  Scenario: Cancel an order
    Given the customer has a pending order
    When the customer cancels the order within the allowed window
    Then the order status changes to cancelled
    And the reserved stock is released
