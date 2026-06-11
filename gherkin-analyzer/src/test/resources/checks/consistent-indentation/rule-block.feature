Feature: Order fulfillment
  Orders are processed through different channels.

  Rule: Standard orders

    Background:
      Given the warehouse system is online

    Scenario: Process a standard order
      Given the order has been placed
      When the warehouse picks the items
      Then the order is shipped within 2 business days

  Rule: Express orders

    Scenario: Process an express order
      Given the order has been placed with express shipping
      When the warehouse prioritizes the order
      Then the order is shipped within 1 business day
