Feature: Account access

  Rule: Customers can sign in

    Scenario: Access the account
      Given the customer has valid credentials
      When the customer signs in
      Then the account dashboard is shown

  Rule: Administrators can sign in

    Scenario: Access the account
      Given the administrator has valid credentials
      When the administrator signs in
      Then the admin dashboard is shown
