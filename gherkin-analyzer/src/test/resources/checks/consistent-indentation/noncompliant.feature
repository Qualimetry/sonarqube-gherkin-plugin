Feature: Customer loyalty programme

# Noncompliant
Scenario: Customer earns points on purchase
# Noncompliant
      Given the customer is enrolled in the loyalty programme
# Noncompliant
  When the customer completes a purchase of $50
# Noncompliant
      Then the customer earns 50 loyalty points
