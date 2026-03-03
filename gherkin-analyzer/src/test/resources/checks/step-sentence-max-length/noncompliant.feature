Feature: Subscription management

  Scenario: Customer upgrades with add-ons
    Given the customer has an active basic subscription
    # Noncompliant
    When the customer navigates to the special promotions page and adds the premium yearly subscription plan with all optional add-ons and extended warranty coverage to their shopping cart
    Then the upgrade is applied immediately
