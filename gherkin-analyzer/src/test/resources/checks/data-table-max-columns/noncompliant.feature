Feature: Wide data tables

  Scenario: Create user with full profile
    # Noncompliant {{Step data table has 12 columns, which exceeds the limit of 10. Consider reducing the number of columns.}}
    Given the following user data:
      | name | email | phone | addr | city | state | zip | country | dob | gender | lang | role |
      | Alice | a@t.c | 555   | Main | NYC  | NY    | 10  | US      | Jan | F      | en   | adm  |
