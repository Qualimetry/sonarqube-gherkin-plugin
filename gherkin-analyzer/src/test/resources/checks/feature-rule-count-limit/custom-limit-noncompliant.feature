# Noncompliant {{This Feature has 3 Rule blocks, which exceeds the limit of 2. Split it into smaller features.}}
Feature: Product catalog

  Rule: Browsing
    Scenario: Browse all products
      Given the catalog contains products
      When the customer views the catalog
      Then all products are displayed

  Rule: Searching
    Scenario: Search products
      Given the catalog contains products
      When the customer searches for "headphones"
      Then matching products are displayed

  Rule: Filtering
    Scenario: Filter by category
      Given the catalog contains products
      When the customer filters by "Electronics"
      Then only electronics products are displayed
