package steps;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.parabank.*;
import util.Driver;

public class ParabankLoginSteps {

    private final WebDriver driver = Driver.getDriver();
    private final LoginPage loginPage = new LoginPage(driver);
    private final AccountsOverviewPage accountsPage = new AccountsOverviewPage(driver);

    @Given("the user is on the Parabank login page")
    public void open_login_page() {

    }

    @When("the user logs into Parabank with username {string} and password {string}")
    public void login_with_credentials(String user, String pass) {

    }

    @Then("the Accounts Overview page should be visible")
    public void verify_accounts_overview() {

    }

    @Then("an error message should be displayed")
    public void verify_error_message() {
    }
}