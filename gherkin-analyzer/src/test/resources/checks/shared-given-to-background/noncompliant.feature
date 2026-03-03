# Noncompliant {{Move the common Given step(s) to a Background section.}}
Feature: Order Processing
  As a customer service representative
  I want to process orders
  So that customers receive their purchases

  Scenario: Process standard order
    Given I am logged in as a customer service representative
    When I select an order
    Then I can view order details

  Scenario: Process priority order
    Given I am logged in as a customer service representative
    When I select a priority order
    Then I can expedite the shipping
