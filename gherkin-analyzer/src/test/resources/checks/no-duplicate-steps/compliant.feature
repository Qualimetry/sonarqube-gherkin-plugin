Feature: Fund Transfer
  As a bank customer
  I want to transfer funds between accounts
  So that I can manage my money

  Scenario: Transfer funds from checking to savings
    Given I am logged in as a customer
    And I have a checking account with $500
    And I have a savings account with $200
    When I transfer $100 from checking to savings
    Then my checking account should have $400
    And my savings account should have $300
