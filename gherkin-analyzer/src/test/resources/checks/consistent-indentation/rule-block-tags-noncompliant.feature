Feature: Order fulfillment

  Rule: Standard orders

    # Noncompliant {{Expected indentation of 4 spaces (level 2), but found 2 spaces.}}
  @smoke
    Scenario: Process a standard order
      Given the order has been placed
      When the warehouse picks the items
      Then the order is shipped within 2 business days

# Noncompliant {{Expected indentation of 2 spaces (level 1), but found 0 spaces.}}
@express
  Rule: Express orders

    # Noncompliant {{Expected indentation of 4 spaces (level 2), but found 6 spaces.}}
      @priority
    Scenario: Process an express order
      Given the order has been placed with express shipping
      When the warehouse prioritizes the order
      Then the order is shipped within 1 business day
