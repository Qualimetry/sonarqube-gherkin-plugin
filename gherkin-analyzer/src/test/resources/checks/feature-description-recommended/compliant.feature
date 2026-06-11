Feature: Subscription management
  Customers can subscribe to monthly or annual plans,
  upgrade or downgrade their subscription tier, and
  cancel their subscription at any time.

  Scenario: Customer subscribes to a monthly plan
    Given the customer is on the pricing page
    When the customer selects the "Professional" monthly plan
    Then the subscription is activated
    And the customer is charged $29.99
