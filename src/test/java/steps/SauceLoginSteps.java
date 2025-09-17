package steps;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import pages.sauce.LoginPage;
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
        // tiny explicit wait loop to avoid implicit waits
        long end = System.currentTimeMillis() + 5000;
        boolean ok = false;
        while (System.currentTimeMillis() < end) {
            try {
                if (loginPage.isProductsPage()) { ok = true; break; }
                Thread.sleep(200);
            } catch (Exception ignored) {}
        }
        Assertions.assertTrue(ok, "Expected to be on Products page.");
    }

    // Optional negative step if you add a scenario outline for invalid creds:
    @When("the user logs in with username {string} and password {string}")
    public void login_with_params(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
    }

    @Then("an error message containing {string} should be shown")
    public void error_message_should_be_shown(String expected) {
        // wait briefly for the error to appear
        long end = System.currentTimeMillis() + 3000;
        String actual = "";
        while (System.currentTimeMillis() < end) {
            actual = loginPage.getErrorText();
            if (!actual.isEmpty()) break;
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
        Assertions.assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected error to contain: " + expected + " but was: " + actual
        );
    }
}