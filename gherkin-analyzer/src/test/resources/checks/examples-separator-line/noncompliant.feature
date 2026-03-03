Feature: Currency conversion

  Scenario Outline: Convert between currencies
    Given the exchange rate from <from> to <to> is <rate>
    When the customer converts <amount> <from>
    Then the result is <expected> <to>
    # Noncompliant
    Examples:
      | from | to  | rate | amount | expected |
      | USD  | EUR | 0.85 | 100    | 85       |
      | GBP  | USD | 1.27 | 200    | 254      |
