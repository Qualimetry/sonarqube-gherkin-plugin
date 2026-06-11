Feature: Account management

  Scenario: Deactivate an account
    Given the user has an active account
    # Noncompliant
    When account is deactivated
    Then the account status is inactive
