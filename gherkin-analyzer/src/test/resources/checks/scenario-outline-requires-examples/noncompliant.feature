Feature: User registration

  # Noncompliant {{Add an Examples section to this Scenario Outline. A Scenario Outline without Examples produces zero test iterations.}}
  Scenario Outline: Register with valid data
    Given the user fills in the registration form
    When the user submits the form
    Then the account is created
