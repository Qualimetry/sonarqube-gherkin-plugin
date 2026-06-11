Feature: Product Search
  As a customer
  I want to search for products
  So that I can find items I want to purchase

  Scenario: Search by product name
    Given I am on the product search page
    When I enter a product name
    Then I should see matching products

  Scenario: Search by category
    Given I am on the product search page
    When I select a category
    Then I should see products in that category

  Scenario: Search with filters
    Given I am on the product search page
    When I apply search filters
    Then I should see filtered results
