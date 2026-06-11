Feature: Discount calculation

  Scenario Outline: Apply discount
    Given a customer of type "<type>"
    When an order of <amount> is placed
    Then the discount is <discount>

    Examples:
      # Noncompliant {{Examples column "type" has the constant value "gold" in every data row. Inline the value into the steps and remove the column.}}
      | type | amount | discount |
      | gold | 100    | 10       |
      | gold | 200    | 20       |
