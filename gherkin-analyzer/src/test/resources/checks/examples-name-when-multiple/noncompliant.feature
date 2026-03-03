Feature: Examples naming - noncompliant

  Scenario Outline: Multiple examples without names
    Given a user "<user>"
    When they perform action "<action>"
    Then the result is "<result>"

    # Noncompliant
    Examples:
      | user  | action | result  |
      | admin | create | success |

    # Noncompliant
    Examples:
      | user  | action | result |
      | guest | create | denied |
