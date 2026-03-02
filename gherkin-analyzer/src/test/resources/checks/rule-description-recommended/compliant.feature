Feature: Loyalty Program
  Reward customers for their continued patronage.

  Rule: Points accumulation
    Customers earn 1 point per dollar spent on qualifying purchases.
    Points are credited within 24 hours of the transaction.

    Scenario: Earn points on purchase
      Given a loyalty member
      When they make a purchase of $50
      Then they earn 50 points

  Rule: Points redemption
    Customers can redeem accumulated points for discounts on future purchases.
    100 points equals $1 in discount value.

    Scenario: Redeem points for discount
      Given a loyalty member with 500 points
      When they redeem 200 points at checkout
      Then they receive a $2 discount
