Feature: Single tags only

  @smoke
  Scenario: Basic search
    Given the search page is loaded
    When the user searches for "test"
    Then results are displayed

  @regression
  Scenario: Advanced search
    Given the search page is loaded
    When the user applies filters
    Then filtered results are displayed
