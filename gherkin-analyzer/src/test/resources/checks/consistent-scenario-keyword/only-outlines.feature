Feature: Only outlines - no regular scenarios

  Scenario Outline: First outline
    Given a user "<user>"
    When they log in
    Then they see "<page>"

    Examples:
      | user  | page      |
      | admin | dashboard |

  Scenario Outline: Second outline
    Given a product "<product>"
    When added to cart
    Then the total is "<total>"

    Examples:
      | product | total |
      | widget  | 9.99  |
