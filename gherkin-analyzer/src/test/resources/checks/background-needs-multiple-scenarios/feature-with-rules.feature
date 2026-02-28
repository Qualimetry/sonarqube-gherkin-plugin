Feature: Feature with Background and Rules
  The Feature Background serves scenarios inside Rules, so it should not be flagged
  even though there is only one Feature-level scenario (there are none here, but Rules exist).

  Background:
    Given a common setup step

  Rule: First business rule
    Scenario: Scenario in first rule
      When the user does something
      Then something happens

  Rule: Second business rule
    Scenario: Scenario in second rule
      When the user does something else
      Then something else happens
