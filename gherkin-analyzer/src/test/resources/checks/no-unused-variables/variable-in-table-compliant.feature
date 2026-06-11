Feature: Bulk account provisioning

  Scenario Outline: Create an account from profile data
    Given the admin opens the provisioning form
    When the admin submits the profile
      | field | value  |
      | name  | <name> |
      | role  | <role> |
    Then the account is created

    Examples:
      | name  | role   |
      | Alice | admin  |
      | Bob   | editor |
