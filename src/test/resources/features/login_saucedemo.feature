@smoke @saucedemo
Feature: Login (Sauce Demo)

  Scenario: Successful login with valid credentials
    Given the user is on the SauceDemo login page
    When the user enters valid SauceDemo credentials
    Then the user should see the products dashboard
  @negative
  Scenario: Login fails with locked out user
    Given the user is on the SauceDemo login page
    When the user logs in with username "locked_out_user" and password "secret_sauce"
    Then an error message containing "Sorry, this user has been locked out." should be shown