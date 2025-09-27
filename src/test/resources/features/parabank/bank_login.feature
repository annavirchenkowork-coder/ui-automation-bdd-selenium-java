Feature: Parabank Login
  Scenario: Successful login
    Given the user is on the Parabank login page
    When the user logs into Parabank with username "..." and password "..."
    Then the Accounts Overview page should be visible

  Scenario: Invalid login
    Given the user is on the Parabank login page
    When the user logs into Parabank with username "..." and password "..."
    Then an error message should be displayed