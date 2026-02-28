Feature: Multi-examples outline

  # Noncompliant {{This Scenario Outline has 2 Examples section(s), each with at most 1 data row(s). Consider using a plain Scenario instead.}}
  Scenario Outline: Login across environments
    Given the user navigates to the "<env>" login page
    When the user enters "<username>" and "<password>"
    Then the user should be authenticated

    Examples: Production
      | env  | username | password |
      | prod | admin    | secret   |

    Examples: Staging
      | env     | username | password |
      | staging | tester   | test123  |
