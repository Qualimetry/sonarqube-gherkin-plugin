Feature: Account management

  Scenario: Deactivate an account
    Given the user has an active account
    When the account is deactivated
    Then the account status is inactive

  Scenario: Reactivate an account
    Given the user has a deactivated account
    When the account is reactivated
    Then the account status is active
