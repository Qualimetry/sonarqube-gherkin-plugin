Feature: Wide Examples table

  Scenario Outline: Create full profile
    Given the user "<name>" with email "<email>"
    When the profile is created
    Then the profile is saved

    # Noncompliant {{Examples data table has 12 columns, which exceeds the limit of 10. Consider reducing the number of columns.}}
    Examples:
      | name | email | phone | addr | city | state | zip | country | dob | gender | lang | role |
      | Alice | a@t.c | 555  | Main | NYC  | NY    | 10  | US      | Jan | F      | en   | adm  |
      | Bob   | b@t.c | 556  | Oak  | LA   | CA    | 20  | US      | Feb | M      | en   | usr  |
