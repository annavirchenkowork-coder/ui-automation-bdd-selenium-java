@parabank @login
Feature: Parabank Login

  Scenario: New user can register and land on Accounts Overview
    Given a fresh Parabank user is registered
    When the user logs into Parabank with those credentials
    Then the Accounts Overview page should be visible

  Scenario: Accessing accounts without login should redirect to login page
    Given the user tries to access the Accounts Overview page directly
    Then the Parabank login page should be visible