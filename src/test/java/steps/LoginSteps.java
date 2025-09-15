package steps;

import io.cucumber.java.After;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import util.Driver;

public class LoginSteps {

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {

    }

    @When("I login with username {string} and password {string}")
    public void i_login(String user, String pass) {

    }

    @Then("I should see the products page")
    public void i_should_see_products() {

    }

    @After
    public void tearDown() {  }
}