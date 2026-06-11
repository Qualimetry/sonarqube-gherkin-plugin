# A comment at the top of the file
@smoke @regression
Feature: Order management
  Manage customer orders in the system

  Background: Common setup
    Given the system is initialized
    And the database is clean

  @happy-path
  Scenario: View order list
    Given a logged-in customer
    When the customer navigates to the order page
    Then the order list is displayed

  @data-driven
  Scenario Outline: Place an order
    Given a customer named "<name>"
    When the customer orders <quantity> items
    Then the order total is "<total>"

    Examples: Standard orders
      | name  | quantity | total  |
      | Alice | 3        | $30.00 |
      | Bob   | 1        | $10.00 |

  Scenario: View order with details
    Given a logged-in customer
    When the customer views order details
    Then the following details are shown:
      | field  | value      |
      | status | Processing |
      | total  | $50.00     |

  Scenario: API response validation
    Given the order API returns:
      """json
      {
        "orderId": "12345",
        "status": "confirmed"
      }
      """
    When the response is processed
    Then the order status is "confirmed"
