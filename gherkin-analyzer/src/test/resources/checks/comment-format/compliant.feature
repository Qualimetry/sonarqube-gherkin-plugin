Feature: Delivery scheduling

  # This comment follows the correct format
  Scenario: Schedule a standard delivery
    Given the customer has placed an order
    And the delivery address is confirmed
    # Delivery window is based on postcode
    When the customer selects standard delivery
    Then the delivery is scheduled within 3-5 business days

  # Another valid comment with proper spacing
  Scenario: Schedule an express delivery
    Given the customer has placed an order
    When the customer selects express delivery
    Then the delivery is scheduled for the next business day
