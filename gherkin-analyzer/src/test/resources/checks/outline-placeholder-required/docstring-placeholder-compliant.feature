Feature: Parameterized request bodies

  Scenario Outline: Submit an order payload
    Given the API is available
    When the client submits the order request
      """
      { "customer": "<customer>", "quantity": <quantity> }
      """
    Then the order is accepted

    Examples:
      | customer | quantity |
      | alice    | 2        |
      | bob      | 5        |

  Scenario Outline: Submit order line items
    Given the API is available
    When the client submits the following line items
      | sku       | amount   |
      | <sku>     | <amount> |
    Then the line items are accepted

    Examples:
      | sku    | amount |
      | A-100  | 1      |
      | B-200  | 3      |
