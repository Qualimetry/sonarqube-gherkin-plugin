# Noncompliant {{Move the common Given step(s) into the existing Background section.}}
Feature: Order Processing

  Background:
    Given the order service is available

  Scenario: Process standard order
    Given I am logged in as a customer service representative
    When I select an order
    Then I can view order details

  Scenario: Process priority order
    Given I am logged in as a customer service representative
    When I select a priority order
    Then I can expedite the shipping
