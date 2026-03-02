Feature: Product returns
  Customers can return products within the return window.

  Scenario: Return an undamaged product
    Given the customer purchased a product within the last 30 days
    When the customer initiates a return
    Then the return is approved and a shipping label is provided
