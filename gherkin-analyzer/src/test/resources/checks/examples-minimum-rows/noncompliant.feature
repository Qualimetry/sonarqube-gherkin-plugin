Feature: Discount calculations

  Scenario Outline: Apply percentage discount
    Given a product priced at <price> dollars
    When a <discount> percent discount is applied
    Then the final price should be <final_price> dollars

    # Noncompliant {{This Examples table has 0 data row(s); at least 2 are required.}}
    Examples:
      | price | discount | final_price |

  Scenario Outline: Apply fixed discount
    Given a product priced at <price> dollars
    When a <discount> dollar discount is applied
    Then the final price should be <final_price> dollars

    # Noncompliant {{This Examples table has 1 data row(s); at least 2 are required.}}
    Examples:
      | price | discount | final_price |
      | 100   | 10       | 90          |
