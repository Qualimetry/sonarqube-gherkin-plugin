Feature: Delivery scheduling

  # Noncompliant
  #This comment lacks a space after the hash
  Scenario: Schedule a standard delivery
    Given the customer has placed an order
    # Noncompliant
    #Another badly formatted comment
    When the customer selects standard delivery
    Then the delivery is scheduled within 3-5 business days
