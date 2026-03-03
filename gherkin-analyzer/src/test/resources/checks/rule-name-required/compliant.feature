Feature: Subscription Management
  Manage customer subscription plans and billing.

  Rule: Monthly billing
    Customers on monthly plans are billed on the same day each month.

    Scenario: Bill on subscription anniversary
      Given a customer subscribed on the 15th
      When the billing cycle runs on the 15th
      Then the customer is charged the monthly rate

  Rule: Annual billing
    Customers on annual plans receive a discounted rate.

    Scenario: Apply annual discount
      Given a customer on an annual plan
      When the billing cycle runs
      Then the customer is charged the discounted annual rate
