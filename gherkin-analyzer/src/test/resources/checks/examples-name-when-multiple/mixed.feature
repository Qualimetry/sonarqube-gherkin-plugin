Feature: Examples naming - mixed

  Scenario Outline: Some examples named some not
    Given a user "<user>"
    When they perform action "<action>"
    Then the result is "<result>"

    Examples: Valid users
      | user  | action | result  |
      | admin | create | success |

    # Noncompliant
    Examples:
      | user  | action | result |
      | guest | create | denied |
