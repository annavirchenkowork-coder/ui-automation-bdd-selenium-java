@regression @saucedemo @sorting
Feature: Sort products on SauceDemo

  Background:
    Given the user is on the SauceDemo login page
    And the user logs in with username "standard_user" and password "secret_sauce"

  Scenario: Sort by Price (low to high)
    When the user sorts products by "Price (low to high)"
    Then the first product price should be "$7.99"