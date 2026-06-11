Feature: Order fulfillment

  Rule: Standard orders

  # Noncompliant {{Expected indentation of 4 spaces (level 2), but found 2 spaces.}}
  Scenario: Process a standard order
    # Noncompliant {{Expected indentation of 6 spaces (level 3), but found 4 spaces.}}
    Given the order has been placed
    # Noncompliant {{Expected indentation of 6 spaces (level 3), but found 4 spaces.}}
    When the warehouse picks the items
    # Noncompliant {{Expected indentation of 6 spaces (level 3), but found 4 spaces.}}
    Then the order is shipped within 2 business days
