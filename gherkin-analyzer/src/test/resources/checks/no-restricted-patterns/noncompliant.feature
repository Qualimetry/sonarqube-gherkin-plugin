Feature: User data retrieval

  Scenario: Fetch user data
    # Noncompliant {{Step text matches the restricted pattern "https?://". Remove or rephrase it.}}
    Given the API at "https://api.example.com/users"
    When the client sends a GET request
    Then the response status is 200
