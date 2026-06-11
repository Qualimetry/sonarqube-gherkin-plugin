Feature: User registration

  Scenario Outline: Register with valid credentials
    Given the user navigates to the registration page
    When the user enters "<username>" and "<email>"
    Then the registration should be successful

    Examples:
      | username | email              |
      | alice    | alice@example.com  |
      | bob      | bob@example.com    |
