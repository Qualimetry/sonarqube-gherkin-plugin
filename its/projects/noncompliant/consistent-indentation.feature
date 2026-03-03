Feature: Customer loyalty programme

Scenario: Customer earns points on purchase
      Given the customer is enrolled in the loyalty programme
  When the customer completes a purchase of $50
      Then the customer earns 50 loyalty points
