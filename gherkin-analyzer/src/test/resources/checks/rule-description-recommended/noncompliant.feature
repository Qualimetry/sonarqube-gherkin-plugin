Feature: Loyalty Program
  Reward customers for their continued patronage.

  # Noncompliant {{Add a description to this Rule.}}
  Rule: Points accumulation
    Scenario: Earn points on purchase
      Given a loyalty member
      When they make a purchase of $50
      Then they earn 50 points
