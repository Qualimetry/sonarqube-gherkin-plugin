Feature: Product filtering

  Scenario: Filter products by category and price
    Given the product catalogue is loaded
    Given the user is on the products page
    When the user selects the electronics category
    When the user sets a price range of $100 to $500
    Then the filtered products are displayed
