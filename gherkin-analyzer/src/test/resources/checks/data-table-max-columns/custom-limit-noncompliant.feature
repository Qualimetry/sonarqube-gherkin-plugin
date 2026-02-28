Feature: Strict column limits

  Scenario: Create user with many columns
    # Noncompliant {{Step data table has 5 columns, which exceeds the limit of 4. Consider reducing the number of columns.}}
    Given the following user data:
      | name  | email      | phone    | city    | role  |
      | Alice | a@test.com | 555-0101 | Seattle | admin |
