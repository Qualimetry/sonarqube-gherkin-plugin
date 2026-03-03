Feature: Compliant verification process

  Scenario: Run compliance checks
    Given the system is configured
    When compliance checks are executed
    Then all checks pass
