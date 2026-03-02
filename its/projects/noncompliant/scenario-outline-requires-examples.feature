Feature: Outline without examples

  Scenario Outline: Purchase product with discount
    Given a product priced at <price> dollars
    When the customer applies discount code <code>
    Then the final price is <final_price> dollars
