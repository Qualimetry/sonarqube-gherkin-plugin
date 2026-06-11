Feature: Discount calculation

  Scenario Outline: Apply discount
    Given a customer of type "<type>"
    When an order of <amount> is placed
    Then the discount is <discount>

    Examples:
      | type   | amount | discount |
      | gold   | 100    | 10       |
      | silver | 200    | 5        |

  Scenario Outline: Single data row is not judged
    Given a customer of type "<type>"
    When an order of <amount> is placed
    Then the discount is <discount>

    Examples:
      | type | amount | discount |
      | gold | 100    | 10       |
