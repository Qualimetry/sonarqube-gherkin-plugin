Feature: Funds transfer

  Scenario: Transfer funds between accounts
    Given the customer has a savings account with $5000
    And the customer has a checking account with $1000
    When the customer transfers $500 from savings to checking
    Then the savings balance should be $4500
    And the checking balance should be $1500
