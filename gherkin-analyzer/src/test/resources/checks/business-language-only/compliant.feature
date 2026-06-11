Feature: Account Balance Inquiry
  As a bank customer
  I want to check my account balance
  So that I know how much money I have available

  Scenario: View account balance
    Given I am logged in as a customer
    When I request my account balance
    Then I should see my current balance
    And the balance should be accurate

  Scenario: View balance for specific account
    Given I am logged in as a customer
    And I have multiple accounts
    When I choose my checking account
    Then I should see the balance for that account
