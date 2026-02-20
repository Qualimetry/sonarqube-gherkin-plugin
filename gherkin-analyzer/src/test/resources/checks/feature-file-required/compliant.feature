Feature: Customer account management
  Customers can manage their account settings including
  personal information and notification preferences.

  Scenario: Customer updates their email address
    Given the customer is logged into their account
    When the customer changes their email to "newemail@example.com"
    Then the email address is updated
    And a verification email is sent to the new address
