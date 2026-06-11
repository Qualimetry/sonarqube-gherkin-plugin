Feature: Comprehensive onboarding

  # Noncompliant
  Scenario: Complete customer onboarding
    Given the customer has created an account
    And the customer has verified their email
    And the customer has accepted the terms
    When the customer enters their first name
    And the customer enters their last name
    And the customer enters their date of birth
    And the customer enters their phone number
    And the customer enters their street address
    And the customer enters their city
    And the customer enters their postal code
    And the customer enters their country
    Then the onboarding is complete
    And the customer receives a welcome notification
