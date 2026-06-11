Feature: Account registration

  Scenario: Customer registers a new account
    Given the customer navigates to the registration page
    And the customer provides valid personal details
    When the customer submits the registration form
    Then a new account is created
    And a welcome email is sent to the customer
