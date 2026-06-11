Feature: Request payloads

  Scenario: Submit a request with a long payload line
    Given a request payload:
      """
      # Noncompliant {{Shorten this line (126 characters; maximum allowed is 120).}}
      yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy
      """
    Then the request is accepted
