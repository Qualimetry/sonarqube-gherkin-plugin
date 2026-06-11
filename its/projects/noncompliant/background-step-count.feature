Feature: Long background test
  Background:
    Given the application is running
    And the database is seeded
    And the cache is cleared
    And the user service is available
    And the notification service is available
    And the audit log is enabled
  Scenario: Basic operation
    When the user performs an action
    Then the action succeeds
