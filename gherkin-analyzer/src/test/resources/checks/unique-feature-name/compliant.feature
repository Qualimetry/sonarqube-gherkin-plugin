Feature: Customer Account Dashboard
  As a customer
  I want to view my account dashboard
  So that I can see my account overview

  Scenario: Display account summary
    Given I am logged in as a customer
    When I navigate to my dashboard
    Then I should see my account summary
    And I should see my recent activity
