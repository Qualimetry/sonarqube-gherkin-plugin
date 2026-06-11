Feature: Customer Support Ticket Management
  As a customer service representative
  I want to manage support tickets
  So that I can help customers resolve their issues

  Scenario: Create a new support ticket
    Given I am logged in as a customer service representative
    When I create a new support ticket
    Then the ticket should be created
    And I should be able to assign it to an agent

  Scenario: Resolve a support ticket
    Given I have an open support ticket
    When I provide a solution to the customer
    Then the ticket should be marked as resolved
