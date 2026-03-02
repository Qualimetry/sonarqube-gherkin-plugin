Feature: User registration

  Scenario Outline: Register with different user types
    Given the registration form is displayed
    When the user registers as a <role> with email <email>
    Then the account is created with <role> permissions

    # Noncompliant {{Add a "email" column to this Examples table.}}
    Examples:
      | role    |
      | admin   |
      | manager |
