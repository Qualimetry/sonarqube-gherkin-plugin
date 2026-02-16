Feature: User registration

  Scenario Outline: Register with <role> role
    Given the user fills in the registration form as <role>
    When the user submits the form
    Then the account is created with role <role>

    Examples:
      | role    |
      | admin   |
      | editor  |
      | viewer  |

  Scenario: Simple registration
    Given the user fills in the registration form
    When the user submits the form
    Then the account is created
