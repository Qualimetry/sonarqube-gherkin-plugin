# Noncompliant {{Add a description to this Feature.}}
Feature: Invoice generation

  Scenario: Generate an invoice for a completed order
    Given a customer has a completed order totaling $150
    When the system generates the invoice
    Then the invoice contains the correct line items and total
