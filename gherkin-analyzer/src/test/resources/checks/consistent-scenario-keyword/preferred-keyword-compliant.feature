Feature: Account management

  Scenario: Open an account
    Given a new customer
    When the customer opens an account
    Then the account is active

  Scenario: Close an account
    Given an active account
    When the customer closes the account
    Then the account is closed

  Scenario Outline: Transfer between accounts
    Given an account with <balance> dollars
    When the customer transfers <amount> dollars
    Then the remaining balance is <remaining> dollars

    Examples:
      | balance | amount | remaining |
      | 100     | 40     | 60        |
      | 50      | 10     | 40        |
