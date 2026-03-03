Feature: Order Processing

  Background:
    Given I am logged in as a customer service representative

  Scenario: Process standard order
    When I select a standard order
    Then I can view order details

  Scenario: Process priority order
    When I select a priority order
    Then I can expedite the shipping
