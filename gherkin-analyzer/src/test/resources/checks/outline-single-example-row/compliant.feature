Feature: User registration

  Scenario Outline: Register with valid credentials
    Given the user navigates to the registration page
    When the user registers with "<username>" and "<password>"
    Then the registration is successful

    Examples:
      | username | password |
      | alice    | pass123  |
      | bob      | pass456  |
      | carol    | pass789  |

  Scenario: Simple login
    Given a registered user
    When the user logs in
    Then the dashboard is displayed
