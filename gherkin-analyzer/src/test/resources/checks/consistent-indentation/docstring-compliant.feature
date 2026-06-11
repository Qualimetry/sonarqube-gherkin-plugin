Feature: API requests

  Scenario: Send a JSON payload
    Given the service is available
    When the client sends the request body
      """
      {
        "name": "sample",
            "deeply": "indented content is part of the value"
      }
      """
    Then the response status is 200

  Rule: Authenticated requests

    Scenario: Send an authenticated payload
      Given the client holds a valid token
      When the client sends the request body
        ```
        plain text content
        ```
      Then the response status is 200
