Feature: Password reset

  Scenario: Customer resets their password
    Given the customer has a registered account
    When the customer requests a password reset
    Then a reset link is sent to the customer's email
    And the link expires after 24 hours
