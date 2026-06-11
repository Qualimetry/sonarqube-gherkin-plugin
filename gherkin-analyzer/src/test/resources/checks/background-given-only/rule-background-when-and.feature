Feature: Order lifecycle

  Rule: Orders are auditable

    Background:
      Given an order exists
      # Noncompliant {{Move this When step out of the Background. Only Given steps are allowed here.}}
      When the order is archived
      # Noncompliant {{Move this And step out of the Background. Only Given steps are allowed here.}}
      And the archive job completes

    Scenario: View an archived order
      Then the order appears in the archive
