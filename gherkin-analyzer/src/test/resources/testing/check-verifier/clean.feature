Feature: User registration

  Scenario: Register a new user
    Given I am on the registration page
    When I fill in valid user details
    Then my account should be created

  Scenario: Attempt duplicate registration
    Given I already have an account
    When I try to register with the same email
    Then I should see an error message
