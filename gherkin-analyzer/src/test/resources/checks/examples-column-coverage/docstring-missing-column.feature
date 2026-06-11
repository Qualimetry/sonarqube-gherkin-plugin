Feature: Email templates

  Scenario Outline: Render a template body
    Given the template for <recipient> is loaded
    When the message body is rendered
      """
      Dear <recipient>, your order <order> has shipped.
      """
    Then the rendered body has no placeholders

    # Noncompliant {{Add a "order" column to this Examples table.}}
    Examples:
      | recipient |
      | Alice     |
      | Bob       |
