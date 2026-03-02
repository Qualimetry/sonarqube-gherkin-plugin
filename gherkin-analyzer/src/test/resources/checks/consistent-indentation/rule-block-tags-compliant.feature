Feature: Order fulfillment

  @standard
  Rule: Standard orders

    @smoke
    Scenario: Process a standard order
      Given the order has been placed
      When the warehouse picks the items
      Then the order is shipped within 2 business days

    @regression
    Scenario: Cancel a standard order
      Given the order has been placed
      When the customer cancels the order
      Then the order is cancelled

  @express
  Rule: Express orders

    @smoke @priority
    Scenario: Process an express order
      Given the order has been placed with express shipping
      When the warehouse prioritizes the order
      Then the order is shipped within 1 business day
