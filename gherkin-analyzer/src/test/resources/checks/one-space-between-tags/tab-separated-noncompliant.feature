Feature: Tab-separated tags

  # Noncompliant {{Use exactly one space between tags on this line.}}
  @smoke	@regression
  Scenario: Tags separated by a single tab
    Given the search page is loaded
    When the user searches for "test"
    Then results are displayed

  # Noncompliant {{Use exactly one space between tags on this line.}}
  @smoke 	@api
  Scenario: Tags separated by mixed space and tab
    Given the search page is loaded
    When the user applies filters
    Then filtered results are displayed
