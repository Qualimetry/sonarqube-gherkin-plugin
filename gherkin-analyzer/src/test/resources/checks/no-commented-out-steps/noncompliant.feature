Feature: Order processing

  Scenario: Process a new order
    Given a customer has items in the cart
    When the customer submits the order
    Then the order is created

  # Noncompliant {{Remove or restore this commented-out step.}}
  # Given the user is logged in
  # Noncompliant {{Remove or restore this commented-out step.}}
  # When the user clicks the button
  # Noncompliant {{Remove or restore this commented-out step.}}
  # Then the page refreshes
