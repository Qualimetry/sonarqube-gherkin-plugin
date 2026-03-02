Feature: Order processing

  # Noncompliant {{This Scenario Outline has 1 Examples section(s), each with at most 2 data row(s). Consider using a plain Scenario instead.}}
  Scenario Outline: Process order with few examples
    Given the user adds "<product>" to the cart
    When the user checks out
    Then the order total is "<total>"

    Examples:
      | product | total  |
      | Widget  | $10.00 |
      | Gadget  | $25.00 |
