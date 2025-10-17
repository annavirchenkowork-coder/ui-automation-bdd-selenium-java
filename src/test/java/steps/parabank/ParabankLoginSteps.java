package steps.parabank;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.parabank.*;
import util.ConfigurationReader;
import util.Driver;

/**
 * Cucumber steps for Parabank auth flows.
 * Minimal state is kept per-scenario (username/password) so we can
 * register a fresh user and then explicitly test the login path.
 */
public class ParabankLoginSteps {

    private final WebDriver driver = Driver.getDriver();
    private final LoginPage loginPage = new LoginPage();
    private final RegisterPage registerPage = new RegisterPage();
    private final AccountsOverviewPage accountsPage = new AccountsOverviewPage();

    private String registeredUsername;
    private String registeredPassword;

    @Given("the user is on the Parabank login page")
    public void open_login_page() {
        loginPage.open();
    }

    @When("the user logs into Parabank with username {string} and password {string}")
    public void login_with_credentials(String user, String pass) {
        loginPage.login(user, pass);
    }

    @Then("the Accounts Overview page should be visible")
    public void verify_accounts_overview() {
        Assertions.assertTrue(accountsPage.isVisible(), "Accounts Overview page is not visible.");
    }

    @Given("the user tries to access the Accounts Overview page directly")
    public void access_overview_directly() {
        driver.get(ConfigurationReader.getProperty("baseUrl.parabank") + "overview.htm");
    }

    @Then("the Parabank login page should be visible")
    public void verify_login_page_visible() {
        Assertions.assertTrue(loginPage.isVisible(), "Parabank login page is not visible.");
    }

    @Given("a fresh Parabank user is registered")
    public void fresh_user_registered() {
        // Generate disposable test creds.
        registeredUsername = "auto_" + System.currentTimeMillis();
        registeredPassword = "Passw0rd!";

        // Navigate to Register and create the account.
        loginPage.open();
        loginPage.goToRegister();
        registerPage.registerMinimal(registeredUsername, registeredPassword);

        // Registration leaves you logged in.
        if (!accountsPage.isVisible()) {
            driver.get(ConfigurationReader.getProperty("baseUrl.parabank") + "overview.htm");
        }
        Assertions.assertTrue(accountsPage.isVisible(), "User not on Accounts Overview after registration.");
    }

    @When("the user logs into Parabank with those credentials")
    public void login_with_generated_credentials() {
        logoutIfLoggedIn();

        loginPage.open();
        loginPage.login(registeredUsername, registeredPassword);
    }

    // -----------------------
    // Helpers
    // -----------------------

    /**
     * Logs out if already authenticated. Keeps scenarios deterministic.
     */
    private void logoutIfLoggedIn() {
        if (accountsPage.isVisible()) {
            try {
                driver.findElement(By.linkText("Log Out")).click();
            } catch (Exception ignored) {
                // If Log Out link isn't present, just proceed to open login.
            }
        }
    }
}