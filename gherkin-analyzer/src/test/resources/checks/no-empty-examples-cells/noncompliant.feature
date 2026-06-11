Feature: Product search

  Scenario Outline: Search for products
    Given the user is on the search page
    When the user searches for "<product>"
    Then "<count>" results are displayed

    # Noncompliant {{Examples table has an empty cell in data row 1, column "count".}}
    Examples:
      | product  | count |
      | laptop   |       |
      | keyboard | 8     |
