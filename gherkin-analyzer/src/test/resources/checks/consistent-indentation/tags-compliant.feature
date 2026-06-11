@smoke @regression
Feature: Customer loyalty programme

  @api-test
  Scenario: Customer earns points on purchase
    Given the customer is enrolled in the loyalty programme
    When the customer completes a purchase of $50
    Then the customer earns 50 loyalty points

  @integration
  Scenario Outline: Customer earns points for different amounts
    Given the customer is enrolled in the loyalty programme
    When the customer completes a purchase of $<amount>
    Then the customer earns <points> loyalty points

    @positive
    Examples:
      | amount | points |
      | 50     | 50     |
      | 100    | 100    |
