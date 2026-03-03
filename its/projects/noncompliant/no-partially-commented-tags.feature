Feature: Partially commented tags

  @smoke @regression # this is a comment on a tag line
  Scenario: Verify partial comment detection
    Given a test scenario
    When the analysis runs
    Then the partial comment is flagged
