Feature: Account management

  Scenario: Deactivate an account
    Given the user has an active account
    When the account is deactivated
    Then the account status is inactive
    # Noncompliant
    And audit log records the change
