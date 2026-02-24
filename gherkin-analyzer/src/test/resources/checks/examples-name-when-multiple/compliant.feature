Feature: Examples naming - compliant

  Scenario Outline: Single examples section needs no name
    Given a user "<user>"
    When they log in
    Then they see the dashboard

    Examples:
      | user  |
      | admin |
      | guest |

  Scenario Outline: Multiple examples sections all named
    Given a user with role "<role>"
    When they access "<page>"
    Then they should see "<result>"

    Examples: Admin access
      | role  | page     | result  |
      | admin | settings | allowed |

    Examples: Guest access
      | role  | page     | result |
      | guest | settings | denied |

  Scenario: Regular scenario without examples
    Given a simple step
    When something happens
    Then it works
