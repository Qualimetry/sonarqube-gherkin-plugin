Feature: Tags repeated on every scenario

  @smoke
  Scenario: First scenario
    Given step one
    Then result one

  @smoke
  Scenario: Second scenario
    Given step two
    Then result two

  @smoke
  Scenario: Third scenario
    Given step three
    Then result three
