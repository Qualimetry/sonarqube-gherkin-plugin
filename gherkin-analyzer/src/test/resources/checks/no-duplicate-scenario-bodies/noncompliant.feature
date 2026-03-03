Feature: User management

  Scenario: Create a new user
    Given an admin is logged in
    When the admin creates a new user
    Then the user is added to the system

  # Noncompliant {{This scenario has an identical step sequence to the scenario at line 3. Consider consolidating.}}
  Scenario: Duplicate user creation
    Given an admin is logged in
    When the admin creates a new user
    Then the user is added to the system
