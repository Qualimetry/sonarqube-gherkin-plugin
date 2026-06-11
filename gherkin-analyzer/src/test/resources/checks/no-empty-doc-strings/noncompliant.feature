Feature: API testing

  Scenario: Submit an empty payload
    Given the API endpoint is available
    # Noncompliant {{Remove or fill in this empty doc string.}}
    When the user submits the following payload:
      """
      """
    Then the response status is 400
