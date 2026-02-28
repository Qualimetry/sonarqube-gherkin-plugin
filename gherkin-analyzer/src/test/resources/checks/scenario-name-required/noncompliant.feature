Feature: Product search

  # Noncompliant {{Add a name to this Scenario.}}
  Scenario:
    Given the product catalog contains 500 items
    When the customer searches for "wireless speaker"
    Then the search results display matching products
