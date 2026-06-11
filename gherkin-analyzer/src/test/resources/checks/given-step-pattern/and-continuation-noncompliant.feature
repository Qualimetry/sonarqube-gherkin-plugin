Feature: Account management

  Scenario: Deactivate an account
    Given the user has an active account
    # Noncompliant
    And user has a pending invoice
    When the account is deactivated
    And confirmation is requested
    Then the account status is inactive
