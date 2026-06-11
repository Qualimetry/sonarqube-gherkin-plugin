# Noncompliant
Feature: Order lifecycle

  Rule: Orders can be placed

    Scenario: Place an order
      Given the cart has items
      When the customer checks out
      Then the order is created

    Scenario: Place an order with a discount
      Given the cart has items and a discount code
      When the customer checks out
      Then the discounted order is created

  Rule: Orders can be cancelled

    Scenario: Cancel an order
      Given an order exists
      When the customer cancels the order
      Then the order status is "Cancelled"
