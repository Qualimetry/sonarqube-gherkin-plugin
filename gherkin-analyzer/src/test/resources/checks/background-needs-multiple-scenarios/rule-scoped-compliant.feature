Feature: Subscription Management

  Rule: Monthly billing
    Customers on monthly plans are billed on the same day each month.

    Background:
      Given a customer on a monthly plan

    Scenario: Bill on anniversary
      When the billing cycle runs
      Then the customer is charged

    Scenario: Skip billing for paused account
      Given the account is paused
      When the billing cycle runs
      Then the customer is not charged
