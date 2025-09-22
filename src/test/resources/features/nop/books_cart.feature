@regression @nop @cart
Feature: Add to cart from Books and verify cart

  Background:
    Given the user is on the nopCommerce home page

  Scenario: Add two items updates the cart badge
    When the user opens the "Books" category
    And the user adds products "Fahrenheit 451 by Ray Bradbury" and "First Prize Pies" to the cart
    Then the cart badge should show "2"

  Scenario: Cart page lists the selected items
    Given the user opens the "Books" category
    And the user adds products "Fahrenheit 451 by Ray Bradbury" and "First Prize Pies" to the cart
    When the user opens the cart
    Then the cart should list items:
      | Fahrenheit 451 by Ray Bradbury |
      | First Prize Pies               |