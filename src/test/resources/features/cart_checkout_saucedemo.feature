@regression @saucedemo @cart @checkout
Feature: Add to cart and proceed to checkout

  Background:
    Given the user is on the SauceDemo login page
    And the user logs in with username "standard_user" and password "secret_sauce"

  @cart
  Scenario: Add two items updates the cart badge
    When the user adds products "Sauce Labs Backpack" and "Sauce Labs Bike Light" to the cart
    Then the cart badge should show "2"

  @cart
  Scenario: Cart page lists the selected items
    Given the user adds products "Sauce Labs Backpack" and "Sauce Labs Bike Light" to the cart
    When the user opens the cart
    Then the cart should list items:
      | Sauce Labs Backpack |
      | Sauce Labs Bike Light |

  @checkout
  Scenario: Checkout overview shows the selected item
    Given the user adds products "Sauce Labs Backpack" to the cart
    And the user opens the cart
    When the user begins checkout with first name "Anna", last name "Virchenko", postal code "22202"
    Then the checkout overview should list items:
      | Sauce Labs Backpack |

  @negative @cart
  Scenario: Removing an item decreases the cart badge
    Given the user adds products "Sauce Labs Backpack" and "Sauce Labs Bike Light" to the cart
    And the cart badge should show "2"
    When the user opens the cart
    And the user removes product "Sauce Labs Bike Light" from the cart
    Then the cart badge should show "1"

  @negative @checkout
  Scenario: Checkout requires mandatory info
    Given the user adds products "Sauce Labs Backpack" to the cart
    And the user opens the cart
    When the user begins checkout with first name "", last name "", postal code ""
    Then a checkout error message containing "Error: First Name is required" should be shown