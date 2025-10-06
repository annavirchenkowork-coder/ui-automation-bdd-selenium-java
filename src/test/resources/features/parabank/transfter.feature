Feature: Parabank Money Transfer
  Scenario: Transfer money between accounts
    Given the user is logged in to Parabank
    When the user navigates to Transfer Funds
    And transfers 100 from account "12345" to account "12456"
    Then the transfer confirmation page should show success