Feature: User authentication

  # Noncompliant
  @regression_test
  # Noncompliant
  @123-priority
  Scenario: User logs in with valid credentials
    Given the user navigates to the login page
    When the user enters valid credentials
    Then the user is authenticated successfully

  # Noncompliant
  @PaymentProcessing
  Scenario: Process payment transaction
    Given the customer has items in their cart
    When the customer completes checkout
    Then the payment is processed successfully
