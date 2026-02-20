Feature: Mixed examples outline

  Scenario Outline: Login with mixed example counts
    Given the user navigates to the "<env>" login page
    When the user enters "<username>" and "<password>"
    Then the user should be authenticated

    Examples: Single row
      | env  | username | password |
      | prod | admin    | secret   |

    Examples: Multiple rows
      | env     | username | password |
      | staging | tester   | test123  |
      | dev     | devuser  | dev456   |
      | qa      | qauser   | qa789    |
