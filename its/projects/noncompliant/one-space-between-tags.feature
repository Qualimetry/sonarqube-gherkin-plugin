Feature: Tag spacing issues

  @smoke  @regression
  Scenario: Verify tag spacing
    Given a test environment
    When the tests are executed
    Then the results are valid

  @api@integration
  Scenario: Verify missing space
    Given an API endpoint
    When a request is sent
    Then the response is successful
