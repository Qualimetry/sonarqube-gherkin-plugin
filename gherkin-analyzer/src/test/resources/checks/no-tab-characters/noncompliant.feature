Feature: Invoice generation

  # Noncompliant
	Scenario: Generate monthly invoice
    # Noncompliant
	  Given the billing cycle has ended
    When the system generates the invoice
    Then the invoice is sent to the customer
