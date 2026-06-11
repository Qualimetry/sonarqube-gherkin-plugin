Feature: Account management


  Scenario: Create account
    Given a new customer
    When the customer fills in registration details
    Then the account is created


  Scenario: Delete account
    Given an existing customer account
    When the customer requests account deletion
    Then the account is removed
