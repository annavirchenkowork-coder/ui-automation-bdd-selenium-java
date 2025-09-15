Feature: Login

  Scenario: Valid login shows account page
    Given I am on the login page
    When I login with username "standard_user" and password "secret_sauce"
    Then I should see the products page