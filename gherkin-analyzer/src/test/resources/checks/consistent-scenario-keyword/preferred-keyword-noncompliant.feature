Feature: Account management

  Scenario: Open an account
    Given a new customer
    When the customer opens an account
    Then the account is active

  # Noncompliant {{Use the configured keyword "Scenario" instead of "Example".}}
  Example: Close an account
    Given an active account
    When the customer closes the account
    Then the account is closed

  # Noncompliant {{Use the configured keyword "Scenario Outline" instead of "Scenario Template".}}
  Scenario Template: Transfer between accounts
    Given an account with <balance> dollars
    When the customer transfers <amount> dollars
    Then the remaining balance is <remaining> dollars

    Examples:
      | balance | amount | remaining |
      | 100     | 40     | 60        |
      | 50      | 10     | 40        |
