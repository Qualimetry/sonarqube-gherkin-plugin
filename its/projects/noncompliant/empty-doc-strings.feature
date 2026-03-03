Feature: Empty doc string test
  Scenario: Request with empty body
    Given the API is available
    When the user sends a request with body
      """
      """
    Then the request fails
