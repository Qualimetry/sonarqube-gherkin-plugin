Feature: User registration

  Scenario Outline: Register with different roles
    Given the registration form is displayed
    When the user registers as a <role> with email <email>
    Then the account is created with <role> permissions

    Examples:
      | role    | email              |
      | admin   | admin@example.com  |
      | editor  | editor@example.com |
