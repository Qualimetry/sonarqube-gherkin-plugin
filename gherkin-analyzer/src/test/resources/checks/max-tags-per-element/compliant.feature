@smoke @regression
Feature: User authentication

  @ui @login
  Scenario: Successful login
    Given a registered user
    When the user logs in
    Then the dashboard is displayed

  @api
  Scenario: API login
    Given a valid API token
    When the client authenticates
    Then the response is 200
