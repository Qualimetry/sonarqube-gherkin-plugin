Feature: Order Processing

  Scenario: Process standard order
    Given the following items are in stock
      | item    | quantity |
      | widget  | 10       |
    When I select an order
    Then I can view order details

  Scenario: Process priority order
    Given the following items are in stock
      | item    | quantity |
      | gadget  | 2        |
    When I select a priority order
    Then I can expedite the shipping
