Feature: Premium membership
  Business rules for premium members

  Rule: Free shipping for premium members

    Background: Premium account
      Given a customer with premium membership

    Scenario: Domestic order qualifies
      When the customer places a domestic order
      Then free shipping is applied

    Scenario: International order excluded
      When the customer places an international order
      Then standard shipping rates apply

  Rule: Loyalty points accumulation

    @points
    Scenario: Earn points on purchase
      Given a registered customer
      When the customer makes a purchase of $100
      Then 100 loyalty points are added
