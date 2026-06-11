Feature: Invoice generation

  Scenario: Generate monthly invoice
    Given the billing cycle has ended
    And all customer transactions have been recorded
    When the system generates the invoice
    Then the invoice total reflects all transactions
    And the invoice is sent to the customer's email

  Scenario: Reissue a corrected invoice
    Given an invoice has been generated with an error
    When the finance team corrects the line item
    Then a revised invoice is issued to the customer
