Feature: Tiered pricing

  Scenario Outline: Price by tier
    Given a "<tier>" customer
    When the price for <quantity> items is calculated
    Then the total is <total>

    Examples: Standard tier
      # Noncompliant {{Examples column "tier" has the constant value "standard" in every data row. Inline the value into the steps and remove the column.}}
      | tier     | quantity | total |
      | standard | 1        | 10    |
      | standard | 2        | 20    |

    Examples: Mixed tiers
      | tier     | quantity | total |
      | standard | 1        | 10    |
      | premium  | 2        | 15    |
