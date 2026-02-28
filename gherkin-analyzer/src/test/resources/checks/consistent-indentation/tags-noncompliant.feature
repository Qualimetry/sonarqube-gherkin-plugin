# Noncompliant {{Expected indentation of 0 spaces (level 0), but found 2 spaces.}}
  @smoke
Feature: Customer loyalty programme

  # Noncompliant {{Expected indentation of 2 spaces (level 1), but found 4 spaces.}}
    @api-test
  Scenario: Customer earns points on purchase
    Given the customer is enrolled in the loyalty programme
    When the customer completes a purchase of $50
    Then the customer earns 50 loyalty points
