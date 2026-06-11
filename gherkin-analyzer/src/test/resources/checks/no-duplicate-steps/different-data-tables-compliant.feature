Feature: Warehouse stock report

  Scenario: Combine stock from two warehouses
    Given the stock includes
      | item   | quantity |
      | laptop | 4        |
    And the stock includes
      | item  | quantity |
      | mouse | 9        |
    When the stock report is generated
    Then both warehouses appear in the report
