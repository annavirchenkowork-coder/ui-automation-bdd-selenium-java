package steps.parabank;

import io.cucumber.java.en.*;
import pages.parabank.*;



public class ParabankTransferSteps {

    private final AccountsOverviewPage overview = new AccountsOverviewPage();
    private final OpenNewAccountPage openAccount = new OpenNewAccountPage();
    private final TransferFundsPage transfer = new TransferFundsPage();
    private final LoginPage login = new LoginPage();

    @Given("the user is logged in to Parabank")
    public void logged_in() {
    }

    @When("the user navigates to Transfer Funds")
    public void go_to_transfer() {
    }

    @When("transfers {int} from account {string} to account {string}")
    public void do_transfer(int amount, String fromAcc, String toAcc) {
    }

    @Then("the transfer confirmation page should show success")
    public void verify_success() {

    }
}