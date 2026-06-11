Feature: Payload validation

  Scenario: Accept the first payload
    Given the service is running
    When the client posts the payload
      """
      {"name": "alpha"}
      """
    Then the response status is accepted

  Scenario: Accept the second payload
    Given the service is running
    When the client posts the payload
      """
      {"name": "beta"}
      """
    Then the response status is accepted
