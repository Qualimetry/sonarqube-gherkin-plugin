Feature: User registration

  Scenario: Register users with a missing email
    # Noncompliant {{Step data table has an empty cell in row 3, column 2.}}
    Given the following users exist:
      | name  | email              |
      | alice | alice@example.test |
      | bob   |                    |
    Then the users are registered

  Scenario: Register a user with a missing name
    # Noncompliant {{Step data table has an empty cell in row 2, column 1.}}
    Given the following users exist:
      | name  | email              |
      |       | carol@example.test |
    Then the users are registered
