Feature: Overloaded Rule Block

  # Noncompliant {{This Rule has 11 scenarios, which exceeds the limit of 10. Decompose it into smaller rules.}}
  Rule: Too many scenarios
    This rule has more than 10 scenarios.

    Scenario: Scenario 1
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 2
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 3
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 4
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 5
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 6
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 7
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 8
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 9
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 10
      Given a precondition
      When an action occurs
      Then a result is expected

    Scenario: Scenario 11
      Given a precondition
      When an action occurs
      Then a result is expected
