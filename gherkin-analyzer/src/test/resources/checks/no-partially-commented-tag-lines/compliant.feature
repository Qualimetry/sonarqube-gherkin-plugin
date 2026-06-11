Feature: User management

  # @slow tag temporarily removed
  @smoke @regression
  Scenario: Create user
    Given an admin user
    When a new user is created
    Then the user appears in the list

  @api
  Scenario: Delete user
    Given an existing user
    When the user is deleted
    Then the user is removed
