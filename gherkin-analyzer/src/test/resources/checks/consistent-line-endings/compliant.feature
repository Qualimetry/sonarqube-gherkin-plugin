Feature: Subscription management

  Scenario: Customer upgrades subscription
    Given the customer has a basic plan
    And the customer is in good standing
    When the customer selects the premium plan
    Then the subscription is upgraded immediately
    And a confirmation email is sent

  Scenario: Customer downgrades subscription
    Given the customer has a premium plan
    When the customer selects the basic plan
    Then the downgrade takes effect at the next billing cycle
