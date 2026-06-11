Feature: Order Processing

  Scenario Outline: Process an order as <role>
    Given I am logged in as a <role>
    When I select an order
    Then I can view order details

    Examples:
      | role           |
      | service agent  |
      | priority agent |

  Scenario Outline: Escalate an order as <role>
    Given I am logged in as a <role>
    When I escalate an order
    Then the order is flagged

    Examples:
      | role           |
      | service agent  |
      | team lead      |
