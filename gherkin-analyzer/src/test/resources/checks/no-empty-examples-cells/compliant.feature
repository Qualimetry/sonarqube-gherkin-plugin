Feature: Product search

  Scenario Outline: Search for products
    Given the user is on the search page
    When the user searches for "<product>"
    Then "<count>" results are displayed

    Examples:
      | product  | count |
      | laptop   | 15    |
      | keyboard | 8     |
