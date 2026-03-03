Feature: Feature with Background but no Scenarios
  This edge case should NOT trigger the background-needs-multiple-scenarios rule
  because the zero-scenario condition is handled by the scenario-required rule.

  Background:
    Given a common setup step
