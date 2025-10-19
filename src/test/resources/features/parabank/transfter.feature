@parabank
Feature: Transfer funds

  Background:
    Given a fresh Parabank user is registered
    And the user is logged in to Parabank

  Scenario: Transfer money between accounts
    When the user navigates to Transfer Funds
    And transfers 100 from any account to a different account
    Then the transfer confirmation page should show success