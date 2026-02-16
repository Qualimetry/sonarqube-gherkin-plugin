Feature: Product catalog

  # Noncompliant {{This Rule has 3 scenarios, which exceeds the limit of 2. Decompose it into smaller rules.}}
  Rule: Product browsing

    Scenario: Browse all products
      Given the catalog contains products
      When the customer views the catalog
      Then all products are displayed

    Scenario: Search products
      Given the catalog contains products
      When the customer searches for "headphones"
      Then matching products are displayed

    Scenario: Filter by category
      Given the catalog contains products
      When the customer filters by "Electronics"
      Then only electronics products are displayed
