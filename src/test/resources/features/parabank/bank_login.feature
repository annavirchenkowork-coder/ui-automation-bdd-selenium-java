Feature: Parabank Login
  Scenario: Successful login
    Given the user is on the Parabank login page
    When the user logs into Parabank with username "demo" and password "demo"
    Then the Accounts Overview page should be visible

  Scenario: Accessing accounts without login should redirect to login page
    Given the user tries to access the Accounts Overview page directly
    Then the Parabank login page should be visible