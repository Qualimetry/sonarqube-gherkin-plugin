Feature: Boundary tag count

  @tag1 @tag2 @tag3 @tag4 @tag5 @tag6 @tag7 @tag8
  Scenario: Exactly at the default limit of 8 tags
    Given a registered user
    When the user logs in
    Then the dashboard is displayed
