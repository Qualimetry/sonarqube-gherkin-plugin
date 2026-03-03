Feature: User login

  @smoke @regression
  Scenario Outline: User logs in with different credentials
    Given the user navigates to login
    When the user enters "<username>" and "<password>"
    Then the user is authenticated

    Examples: Valid users
      | username | password |
      | alice    | secret   |
      | bob      | secret   |

  Scenario Outline: User login with invalid credentials
    Given the user navigates to login
    When the user enters "<username>" and "<password>"
    Then the user sees an error message

    Examples: Invalid users
      | username | password |
      | alice    | wrong    |
      | bob      | wrong    |
