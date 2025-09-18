@regression @saucedemo @sorting
Feature: Sort products on SauceDemo

  Background:
    Given the user is on the SauceDemo login page
    And the user logs in with username "standard_user" and password "secret_sauce"

  Scenario: Sort by Price (low to high)
    When the user sorts products by "Price (low to high)"
    Then the first product price should be "$7.99"

  Scenario: Sorting by Price (low to high) should be non-decreasing
    When the user sorts products by "Price (low to high)"
    Then all visible product prices should be in non-decreasing order

  Scenario: Sorting changes the order when switching from Low to High to High to Low
    When the user sorts products by "Price (low to high)"
    And the user remembers the first product price
    And the user sorts products by "Price (high to low)"
    Then the first product price should not equal the remembered price