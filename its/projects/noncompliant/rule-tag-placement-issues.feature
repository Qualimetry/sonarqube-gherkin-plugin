Feature: Common tags on all scenarios within a rule

  Rule: Authentication rules

    @regression
    Scenario: First scenario in rule
      Given step one
      Then result one

    @regression
    Scenario: Second scenario in rule
      Given step two
      Then result two

    @regression
    Scenario: Third scenario in rule
      Given step three
      Then result three
