Feature: Subscription Management

  Rule: Monthly billing
    Customers on monthly plans are billed on the same day each month.

    # Noncompliant {{This Background serves only one scenario within this Rule. Inline the Background steps into the scenario for clarity.}}
    Background:
      Given a customer on a monthly plan

    Scenario: Bill on anniversary
      When the billing cycle runs
      Then the customer is charged
