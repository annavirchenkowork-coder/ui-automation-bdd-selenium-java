package steps.parabank;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import pages.parabank.*;
import util.ConfigurationReader;
import util.Driver;

public class ParabankLoginSteps {

    private final WebDriver driver = Driver.getDriver();
    private final LoginPage loginPage = new LoginPage(driver);
    private final AccountsOverviewPage accountsPage = new AccountsOverviewPage(driver);

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
        driver.get(ConfigurationReader.getProperty("baseUrl.parabank") + "/parabank/overview.htm");
    }

    @Then("the Parabank login page should be visible")
    public void verify_login_page_visible() {
        Assertions.assertTrue(loginPage.isVisible(), "Parabank login page is not visible.");
    }


}