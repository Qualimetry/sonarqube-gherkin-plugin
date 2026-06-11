Feature: Discount calculations

  Scenario Outline: Apply percentage discount
    Given a product priced at <price> dollars
    When a <discount> percent discount is applied
    Then the final price should be <final_price> dollars

    Examples:
      | price | discount | final_price |
      | 100   | 10       | 90          |
      | 200   | 25       | 150         |
