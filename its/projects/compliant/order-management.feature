@orders
Feature: Order management
  Customers can create, view, and cancel orders through
  the e-commerce platform.

  Background:
    Given the customer is logged in
    And the product catalogue is available

  Scenario: Place a new order
    Customer completes checkout and receives confirmation.
    Given the customer has items in the shopping cart
    When the customer proceeds to checkout
    And the customer confirms the order
    Then the order is created with status "pending"
    And the customer receives an order confirmation email

  Scenario: View order history
    Customer can see a list of past orders.
    Given the customer has placed previous orders
    When the customer navigates to the order history page
    Then a list of all past orders is displayed

  Scenario: Cancel a pending order
    Customer can cancel and get a refund.
    Given the customer has a pending order
    When the customer requests cancellation
    Then the order status changes to "cancelled"
    And the payment is refunded
