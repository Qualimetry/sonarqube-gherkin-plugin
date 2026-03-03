Feature: API testing

  Scenario: Submit a JSON payload
    Given the API endpoint is available
    When the user submits the following payload:
      """json
      {
        "name": "Alice",
        "email": "alice@example.com"
      }
      """
    Then the response status is 200
