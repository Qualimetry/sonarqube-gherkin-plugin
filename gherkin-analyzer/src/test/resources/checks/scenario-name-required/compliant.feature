Feature: Product search

  Scenario: Customer searches by keyword
    Given the product catalog contains 500 items
    When the customer searches for "wireless speaker"
    Then the search results display matching products

  Scenario: Customer filters search results by price
    Given the customer has search results displayed
    When the customer filters results to show items under $50
    Then only products priced below $50 are displayed
