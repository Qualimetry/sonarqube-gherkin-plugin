Feature: User Account Management
  As a bank customer
  I want to manage my account settings
  So that I can control my banking preferences

  Background:
    Given I am logged in as a customer
    And my account balance is $1000
    And I have a savings account

  Scenario: Update email address
    When I update my email address to "newemail@example.com"
    Then my email address should be updated
    And I should receive a confirmation email

  Scenario: Change password
    When I change my password
    Then my password should be updated
    And I should be logged out
