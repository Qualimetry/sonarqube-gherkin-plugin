Feature: Inventory Management
  Track and manage warehouse inventory levels.

  Rule: Stock replenishment
    Automatically reorder items when stock falls below threshold.

    Scenario: Trigger reorder when stock is low
      Given a product with a reorder threshold of 10
      And the current stock is 5
      When the inventory check runs
      Then a reorder request is created

  Rule: Stock audit
    Periodic audits ensure inventory accuracy.

    Scenario: Complete a stock audit
      Given a scheduled stock audit
      When the warehouse staff counts all items
      Then the system inventory matches the physical count
