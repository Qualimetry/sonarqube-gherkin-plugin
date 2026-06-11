Feature: User registration

  Scenario: Register users
    Given the following users exist:
      | name  | email              |
      | alice | alice@example.test |
      | bob   | bob@example.test   |
    Then the users are registered

  Scenario: Step without a data table
    Given the registration service is running
    Then no users are registered

  Scenario: Zero values are not empty
    Given the following balances exist:
      | name  | balance |
      | alice | 0       |
    Then the balances are recorded
