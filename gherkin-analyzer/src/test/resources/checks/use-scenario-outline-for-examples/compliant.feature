Feature: User Login
  As a user
  I want to log in to the system
  So that I can access my account

  Scenario Outline: Login with different user types
    Given I am on the login page
    When I enter username "<username>" and password "<password>"
    Then I should be logged in as "<user_type>"

    Examples:
      | username      | password | user_type |
      | admin@test.com| admin123 | administrator |
      | user@test.com | user123  | customer |
      | guest@test.com| guest123 | guest |
