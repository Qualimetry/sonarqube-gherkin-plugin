Feature: Subscription management

  Scenario: Customer upgrades subscription
    Given the customer has a basic plan
    When the customer selects the premium plan
    Then the subscription is upgraded
