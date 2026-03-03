Feature: Account management

  Scenario: Deactivate an account
    Given the user has an active account
    When the account is deactivated
    # Noncompliant
    Then account status is inactive
