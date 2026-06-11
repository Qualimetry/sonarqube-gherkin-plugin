Feature: Business rules validation

  Rule:

  Rule: Discount rules
    Scenario: Apply 10 percent discount
      Given a cart total above $100
      When the discount is applied
      Then the total is reduced by 10 percent

  Rule: Discount rules

  Rule: Tax calculation
