Feature: Order Processing

  Rule: Standard orders

    Background:
      Given I am logged in as a customer service representative

    Scenario: Process standard order
      When I select a standard order
      Then I can view order details

    Scenario: Process batch order
      When I select multiple orders
      Then I can process them in bulk

  Rule: Priority orders

    Background:
      Given I am logged in as a priority handler

    Scenario: Process priority order
      When I select a priority order
      Then I can expedite the shipping

    Scenario: Process emergency order
      When I select an emergency order
      Then I can arrange same-day delivery
