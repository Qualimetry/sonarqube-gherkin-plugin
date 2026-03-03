Feature: Examples table issues

  Scenario Outline: Verify user roles
    Given a user with role <role>
    When the user accesses the <page> page
    Then access is <result>

    Examples:
      | role    | page      | result  | unused_column |
      | admin   | dashboard | granted | extra         |
      | guest   | settings  | denied  | extra         |
