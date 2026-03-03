Feature: Account management

  Scenario: Deactivate an account
    # Noncompliant
    Given user has an active account
    When the account is deactivated
    Then the account status is inactive
