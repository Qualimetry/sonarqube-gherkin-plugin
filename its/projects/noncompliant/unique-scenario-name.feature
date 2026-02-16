Feature: Report generation

  Scenario: Generate report
    Given a user with reporting access
    When the user requests a sales report
    Then the report is generated

  Scenario: Generate report
    Given a user with admin access
    When the user requests an inventory report
    Then the report is generated
