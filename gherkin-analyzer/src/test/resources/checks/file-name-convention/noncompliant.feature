Feature: Account registration

  Scenario: Customer registers a new account
    Given the customer navigates to the registration page
    When the customer submits the registration form
    Then a new account is created
