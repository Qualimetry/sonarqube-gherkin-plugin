Feature: User registration

  Scenario Outline: Register with valid credentials
    Given the user navigates to the registration page
    When the user enters "<username>" and "<username>"
    Then the registration should be successful

    # Noncompliant {{Remove duplicate Examples header "username".}}
    Examples:
      | username | email             | username |
      | alice    | alice@example.com | alice    |
      | bob      | bob@example.com   | bob      |
