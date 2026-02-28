Feature: User authentication

  # Noncompliant {{Add a description to this scenario to explain its purpose.}}
  Scenario: Successful login
    Given the user is on the login page
    When the user enters valid credentials
    Then the user is redirected to the dashboard

  # Noncompliant {{Add a description to this scenario to explain its purpose.}}
  Scenario Outline: Login with different roles
    Given the user is on the login page
    When the user logs in as "<role>"
    Then the user sees the "<dashboard>" dashboard

    Examples:
      | role  | dashboard |
      | admin | admin     |
      | user  | user      |
