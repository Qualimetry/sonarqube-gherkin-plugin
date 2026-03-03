Feature: Currency conversion

  Scenario Outline: Convert between currencies
    Given the exchange rate from <from> to <to> is <rate>
    When the customer converts <amount> <from>
    Then the result is <expected> <to>

    Examples:
      | from | to  | rate | amount | expected |
      | USD  | EUR | 0.85 | 100    | 85       |
      | GBP  | USD | 1.27 | 200    | 254      |

  Scenario Outline: Apply discount by tier
    Given the customer is in the <tier> loyalty tier
    When the customer purchases an item priced at <price>
    Then a <discount> percent discount is applied

    Examples:
      | tier     | price | discount |
      | Silver   | 100   | 5        |
      | Gold     | 100   | 10       |
      | Platinum | 100   | 15       |
