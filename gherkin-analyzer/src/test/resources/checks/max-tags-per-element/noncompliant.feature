Feature: Over-tagged scenarios

  @smoke @regression @integration @ui @api @nightly @performance @security @critical @login
  # Noncompliant {{Scenario has 10 tags, which exceeds the limit of 8. Reduce the number of tags.}}
  Scenario: Login with too many tags
    Given a registered user
    When the user logs in
    Then the dashboard is displayed
