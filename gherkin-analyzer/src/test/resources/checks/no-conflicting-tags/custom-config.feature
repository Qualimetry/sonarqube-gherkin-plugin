Feature: Tag testing

  @manual @automated
  # Noncompliant {{Tags "@manual" and "@automated" conflict and should not appear together.}}
  Scenario: Conflicting automation tags
    Given a test scenario
    When the test is executed
    Then the result is verified
