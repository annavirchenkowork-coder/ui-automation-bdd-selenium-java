package steps.parabank;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.parabank.*;
import util.ConfigurationReader;
import util.Driver;

/**
 * Step definitions for Parabank authentication and login-related scenarios.
 * Demonstrates a stable Cucumber + Selenium integration with minimal state handling.
 */
public class ParabankLoginSteps {

    private final WebDriver driver = Driver.getDriver();
    private final LoginPage loginPage = new LoginPage();
    private final RegisterPage registerPage = new RegisterPage();
    private final AccountsOverviewPage accountsPage = new AccountsOverviewPage();

    private String registeredUsername;
    private String registeredPassword;

    /** Opens the Parabank login page. */
    @Given("the user is on the Parabank login page")
    public void open_login_page() {
        loginPage.open();
    }

    /** Logs in using provided credentials from the feature file. */
    @When("the user logs into Parabank with username {string} and password {string}")
    public void login_with_credentials(String user, String pass) {
        loginPage.login(user, pass);
    }

    /** Verifies that the login succeeded by checking the Accounts Overview page. */
    @Then("the Accounts Overview page should be visible")
    public void verify_accounts_overview() {
        Assertions.assertTrue(accountsPage.isVisible(), "Accounts Overview page is not visible.");
    }

    /** Attempts to access Accounts Overview directly without login. */
    @Given("the user tries to access the Accounts Overview page directly")
    public void access_overview_directly() {
        driver.get(ConfigurationReader.getProperty("baseUrl.parabank") + "overview.htm");
    }

    /** Ensures redirection to login page when not authenticated. */
    @Then("the Parabank login page should be visible")
    public void verify_login_page_visible() {
        Assertions.assertTrue(loginPage.isVisible(), "Parabank login page is not visible.");
    }

    /**
     * Registers a new user for isolated test runs.
     * Each scenario generates disposable credentials to prevent collisions.
     */
    @Given("a fresh Parabank user is registered")
    public void fresh_user_registered() {
        registeredUsername = "auto_" + System.currentTimeMillis();
        registeredPassword = "Passw0rd!";

        loginPage.open();
        loginPage.goToRegister();
        registerPage.registerMinimal(registeredUsername, registeredPassword);

        // Registration flow ends on Accounts Overview by default
        if (!accountsPage.isVisible()) {
            driver.get(ConfigurationReader.getProperty("baseUrl.parabank") + "overview.htm");
        }
        Assertions.assertTrue(accountsPage.isVisible(), "User not on Accounts Overview after registration.");
    }

    /** Logs in using the dynamically created test credentials. */
    @When("the user logs into Parabank with those credentials")
    public void login_with_generated_credentials() {
        logoutIfLoggedIn();
        loginPage.open();
        loginPage.login(registeredUsername, registeredPassword);
    }

    /** Ensures the test user is logged in — handles self-registration if needed. */
    @Given("the user is logged in to Parabank")
    public void user_is_logged_in_to_parabank() {
        String username = "demo_" + System.currentTimeMillis();
        String password = "demo";

        ParabankAuth.ensureRegisteredAndLoggedIn(username, password);
        Assertions.assertTrue(
                new AccountsOverviewPage().isVisible(),
                "Accounts Overview not visible after login."
        );
    }

    // -----------------------
    // Helpers
    // -----------------------

    /** Logs out if already authenticated to ensure clean test state. */
    private void logoutIfLoggedIn() {
        if (accountsPage.isVisible()) {
            try {
                driver.findElement(By.linkText("Log Out")).click();
            } catch (Exception ignored) {
                // Safe to ignore if the link is missing
            }
        }
    }
}