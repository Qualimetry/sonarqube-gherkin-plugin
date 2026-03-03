Feature: Order Processing

  # Noncompliant {{Move the common Given step(s) to a Background section within this Rule.}}
  Rule: Standard orders

    Scenario: Process standard order
      Given I am logged in as a customer service representative
      When I select a standard order
      Then I can view order details

    Scenario: Process batch order
      Given I am logged in as a customer service representative
      When I select multiple orders
      Then I can process them in bulk
