Feature: E-commerce Shopping Cart
  As a customer
  I want to manage my shopping cart
  So that I can purchase items

  Background:
    Given I am logged in as a customer
    And I have items in my cart
    # Noncompliant {{Move this When step out of the Background. Only Given steps are allowed here.}}
    When I view my cart
    # Noncompliant {{Move this Then step out of the Background. Only Given steps are allowed here.}}
    Then my cart should be displayed

  Scenario: Add item to cart
    When I add a product to my cart
    Then the item should appear in my cart
