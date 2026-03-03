Feature: Duplicate tags on Rule block

  # Noncompliant
  @api @api
  Rule: Business rule with duplicated tags

    Scenario: Scenario inside rule
      Given a step
      When something happens
      Then it works
