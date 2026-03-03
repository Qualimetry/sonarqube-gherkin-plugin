Feature: User management

  # Noncompliant {{Do not mix tags and comments on the same line; move the comment to a separate line.}}
  @smoke @regression # temporarily disabled @slow
  Scenario: Create user
    Given an admin user
    When a new user is created
    Then the user appears in the list

  # Noncompliant {{Do not mix tags and comments on the same line; move the comment to a separate line.}}
  @api #@deprecated
  Scenario: Delete user
    Given an existing user
    When the user is deleted
    Then the user is removed
