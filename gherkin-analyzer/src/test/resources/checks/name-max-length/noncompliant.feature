# Noncompliant {{Feature name is 153 characters long, which exceeds the limit of 120. Shorten the name.}}
Feature: This is an extremely long feature name that goes on and on describing every little detail of the entire feature which is very hard to read in reports now

  # Noncompliant {{Scenario name is 158 characters long, which exceeds the limit of 120. Shorten the name.}}
  Scenario: Verify that when a user enters their username and password and clicks the login button then they are redirected to the main dashboard page showing all widgets
    Given a registered user
    When the user logs in
    Then the dashboard is displayed
