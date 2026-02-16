# Noncompliant
@smoke @regression @smoke
Feature: No duplicate tags - noncompliant

  # Noncompliant
  @critical @critical
  Scenario: First scenario
    Given a step
    When something happens
    Then it works

  Scenario Outline: Parameterized scenario
    Given a user "<user>"
    When they log in
    Then they see "<page>"

    # Noncompliant
    @fast @reliable @fast
    Examples: Roles
      | user  | page      |
      | admin | dashboard |
