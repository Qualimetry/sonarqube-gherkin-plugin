# Noncompliant
Feature: Product catalog

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
