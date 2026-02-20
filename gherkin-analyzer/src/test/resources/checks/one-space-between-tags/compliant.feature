Feature: Search functionality

  @smoke @regression
  Scenario: Basic search
    Given the search page is loaded
    When the user searches for "test"
    Then results are displayed

  @smoke @integration @api
  Scenario: Advanced search
    Given the search page is loaded
    When the user applies filters
    Then filtered results are displayed
