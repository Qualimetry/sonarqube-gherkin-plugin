Feature: User management

  Scenario: Create users
    Given the following users:
      | name  | email       | role  |
      | Alice | a@test.com  | admin |
      | Bob   | b@test.com  | user  |

  Scenario Outline: Login with credentials
    Given the user "<name>" logs in with "<password>"
    Then the user should see the "<page>"

    Examples:
      | name  | password | page      |
      | Alice | pass123  | dashboard |
      | Bob   | pass456  | home      |
