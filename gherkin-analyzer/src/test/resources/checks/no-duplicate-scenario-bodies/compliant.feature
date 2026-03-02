Feature: User management

  Scenario: Create a new user
    Given an admin is logged in
    When the admin creates a new user
    Then the user is added to the system

  Scenario: Deactivate a user
    Given an admin is logged in
    When the admin deactivates a user
    Then the user can no longer log in
