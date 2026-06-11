Feature: User registration

  Scenario: Register a new user
    Given the user clicks the button with id "register-btn"
    When the user fills in the input field with CSS selector ".email-input"
    Then the HTTP response code should be 200
