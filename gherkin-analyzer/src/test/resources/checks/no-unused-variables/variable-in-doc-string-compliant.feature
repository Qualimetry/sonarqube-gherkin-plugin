Feature: Notification templates

  Scenario Outline: Send a templated message
    Given a registered user named <name>
    When the notification is sent
      """
      Hello <name>, your verification code is <code>
      """
    Then the delivery succeeds

    Examples:
      | name  | code |
      | Alice | 1234 |
      | Bob   | 5678 |
