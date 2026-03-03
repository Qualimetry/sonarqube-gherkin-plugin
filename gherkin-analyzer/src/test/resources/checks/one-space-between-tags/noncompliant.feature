Feature: Search functionality

  # Noncompliant {{Use exactly one space between tags on this line.}}
  @smoke  @regression
  Scenario: Basic search
    Given the search page is loaded
    When the user searches for "test"
    Then results are displayed

  # Noncompliant {{Use exactly one space between tags on this line.}}
  @smoke    @integration    @api
  Scenario: Advanced search
    Given the search page is loaded
    When the user applies filters
    Then filtered results are displayed
