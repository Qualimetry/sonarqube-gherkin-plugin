Feature: Rules

  # Noncompliant {{Rule name is 51 characters long, which exceeds the limit of 50. Shorten the name.}}
  Rule: This rule name is deliberately long to exceed limit
    Scenario: A scenario inside the rule
      Given a precondition
      When an action occurs
      Then a result is expected
