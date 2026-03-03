Feature: Currency conversion

  Scenario Outline: Convert <amount> <from> to <to>
    Given the exchange rate from <from> to <to> is <rate>
    When the customer converts <amount> <from>
    Then the result is <expected> <to>

    Examples:
      | from | to  | rate | amount | expected |
      | USD  | EUR | 0.85 | 100    | 85       |
      | GBP  | USD | 1.27 | 200    | 254      |

  Scenario: Simple non-outline scenario
    Given a fixed setup
    When an action is performed
    Then the expected result occurs
