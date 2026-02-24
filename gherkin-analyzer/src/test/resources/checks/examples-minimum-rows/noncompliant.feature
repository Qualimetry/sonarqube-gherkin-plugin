Feature: Discount calculations

  Scenario Outline: Apply percentage discount
    Given a product priced at <price> dollars
    When a <discount> percent discount is applied
    Then the final price should be <final_price> dollars

    # Noncompliant {{Add at least one data row to this Examples table.}}
    Examples:
      | price | discount | final_price |
