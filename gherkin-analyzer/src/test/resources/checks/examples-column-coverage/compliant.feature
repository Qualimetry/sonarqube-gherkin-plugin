Feature: User registration

  Scenario Outline: Register with different user types
    Given the registration form is displayed
    When the user registers as a <role> with email <email>
    Then the account is created with <role> permissions

    Examples:
      | role    | email              |
      | admin   | admin@example.com  |
      | manager | mgr@example.com    |
