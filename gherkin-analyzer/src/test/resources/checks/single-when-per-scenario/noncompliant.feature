Feature: Funds transfer

  # Noncompliant
  Scenario: Transfer and verify funds
    Given the customer has a savings account with $5000
    When the customer transfers $500 from savings to checking
    Then the savings balance should be $4500
    When the customer views the checking account
    Then the checking balance should be $1500
