Feature: User Profile Management
  As a user
  I want to manage my profile
  So that I can keep my information up to date

  # Noncompliant
  Scenario: View profile without action
    Given I am logged in as a user
    Then I should see my profile information

  # Noncompliant
  Scenario: Update profile without verification
    Given I am logged in as a user
    When I update my profile information
