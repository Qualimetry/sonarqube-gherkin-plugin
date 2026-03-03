Feature: User registration

  Scenario Outline: Register with different roles
    Given the registration form is displayed
    When the user registers as a <role>
    Then the account is created

    # Noncompliant {{Remove the unused "unused" column from this Examples table.}}
    Examples:
      | role    | unused   |
      | admin   | foo      |
      | editor  | bar      |
