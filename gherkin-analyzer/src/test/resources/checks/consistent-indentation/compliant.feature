Feature: Customer loyalty programme

  Scenario: Customer earns points on purchase
    Given the customer is enrolled in the loyalty programme
    And the customer has a verified account
    When the customer completes a purchase of $50
    Then the customer earns 50 loyalty points
    And the points appear in the customer's balance

  Scenario: Customer redeems points for a discount
    Given the customer has 500 loyalty points
    When the customer applies points at checkout
    Then a $5 discount is applied to the order
