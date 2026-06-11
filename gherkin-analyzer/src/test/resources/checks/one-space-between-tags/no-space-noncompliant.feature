Feature: Missing space between tags

  # Noncompliant {{Use exactly one space between tags on this line.}}
  @smoke@regression
  Scenario: Search with missing space
    Given the search page is loaded
    When the user searches for "test"
    Then results are displayed
