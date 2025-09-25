package steps;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.sauce.LoginPage;
import util.BrowserUtil;
import util.ConfigurationReader;
import util.Driver;

public class SauceLoginSteps {

    private final WebDriver driver = Driver.getDriver();
    private final LoginPage loginPage = new LoginPage(driver);

    @Given("the user is on the SauceDemo login page")
    public void user_on_login_page() {
        String url = ConfigurationReader.getProperty("baseUrl.saucedemo");
        loginPage.open(url);
    }

    @When("the user enters valid SauceDemo credentials")
    public void enters_valid_credentials() {
        String u = ConfigurationReader.getProperty("saucedemo.username");
        String p = ConfigurationReader.getProperty("saucedemo.password");
        loginPage.enterUsername(u);
        loginPage.enterPassword(p);
        loginPage.clickLogin();
    }

    @Then("the user should see the products dashboard")
    public void should_see_products_dashboard() {
        BrowserUtil.waitForVisibility(By.cssSelector(".title"), 5);
        Assertions.assertTrue(loginPage.isProductsPage(), "Expected to be on Products page.");
    }

    @When("the user logs in with username {string} and password {string}")
    public void login_with_params(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
    }

    @Then("an error message containing {string} should be shown")
    public void error_message_should_be_shown(String expected) {
        String actual = BrowserUtil.waitForVisibility(By.cssSelector("[data-test='error']"), 3).getText().trim();
        Assertions.assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected error to contain: " + expected + " but was: " + actual);
    }
}