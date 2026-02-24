Feature: Strict tag limits

  @smoke @regression @ui @api
  # Noncompliant {{Scenario has 4 tags, which exceeds the limit of 3. Reduce the number of tags.}}
  Scenario: Login with strict limit
    Given a registered user
    When the user logs in
    Then the dashboard is displayed
