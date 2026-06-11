Feature: API requests

  Scenario: Send a JSON payload
    Given the service is available
    When the client sends the request body
    # Noncompliant {{Expected indentation of 6 spaces (level 3), but found 4 spaces.}}
    """
    {
      "key": "value"
    }
      # Noncompliant {{Expected indentation of 6 spaces (level 3), but found 4 spaces.}}
    """
    Then the response status is 200
