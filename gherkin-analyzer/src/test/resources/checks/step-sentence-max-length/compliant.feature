Feature: Subscription management

  Scenario: Customer upgrades subscription
    Given the customer has an active basic subscription
    And the customer is in good standing
    When the customer selects the premium yearly plan
    And the customer adds all optional add-ons
    Then the upgrade is applied immediately
    And a confirmation email is sent to the customer

  Scenario: Customer cancels subscription
    Given the customer has an active subscription
    When the customer requests cancellation
    Then the subscription is cancelled at the end of the billing period
