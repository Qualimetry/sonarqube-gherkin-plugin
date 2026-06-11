Feature: Frobnicator configuration

  Scenario: Enable the Frobnicator module
    Given the Frobnicator module is installed
    When the administrator enables the Frobnicator
    Then the Frobnicator status is active
