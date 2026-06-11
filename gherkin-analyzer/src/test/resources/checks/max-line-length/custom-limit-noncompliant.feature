Feature: Short limit

  Scenario: Short lines only
    Given a short step
    # Noncompliant
    Given a step that exceeds the forty character limit
    Then it is flagged
