Feature: E-commerce Checkout

  Scenario: Complete purchase flow
    Given I am logged in as a customer
    And I have items in my cart
    # Noncompliant
    When I click on the checkout option
    # Noncompliant
    And I submit my payment details
    Then my order should be placed successfully
