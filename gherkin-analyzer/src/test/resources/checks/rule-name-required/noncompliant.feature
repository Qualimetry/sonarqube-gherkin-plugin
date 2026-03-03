Feature: Subscription Management
  Manage customer subscription plans and billing.

  # Noncompliant {{Add a name to this Rule.}}
  Rule:
    Scenario: Bill on subscription anniversary
      Given a customer subscribed on the 15th
      When the billing cycle runs on the 15th
      Then the customer is charged the monthly rate
