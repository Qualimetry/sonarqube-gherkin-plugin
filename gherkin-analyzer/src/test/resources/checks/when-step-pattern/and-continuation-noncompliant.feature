Feature: Account management

  Scenario: Deactivate an account
    Given the user has an active account
    When the account is deactivated
    # Noncompliant
    And confirmation is requested
    Then the account status is inactive
