Feature: Rule tag redundancy

  @premium
  Rule: Premium customer benefits

    @premium
    Scenario: Premium customer gets free shipping
      Given a premium customer
      When the customer places an order
      Then free shipping is applied

    Scenario: Premium customer gets priority support
      Given a premium customer
      When the customer contacts support
      Then the request is prioritised
