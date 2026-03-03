Feature: Payment Processing
  As a customer
  I want to complete payments
  So that I can purchase items

  Scenario: Successful credit card payment
    Given I have items in my cart
    And I am on the checkout page
    When I enter my credit card information
    And I submit the payment
    Then the payment should be processed
    And I should receive a confirmation

  Scenario: Failed payment due to insufficient funds
    Given I have items in my cart
    And I am on the checkout page
    When I attempt to pay with insufficient funds
    Then the payment should be declined
    And I should see an error message
