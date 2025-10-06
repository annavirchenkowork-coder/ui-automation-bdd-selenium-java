package steps.parabank;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import pages.parabank.*;

import java.util.List;


public class ParabankTransferSteps {

    private final AccountsOverviewPage overview = new AccountsOverviewPage();
    private final OpenNewAccountPage openAccount = new OpenNewAccountPage();
    private final TransferFundsPage transfer = new TransferFundsPage();
    private final LoginPage login = new LoginPage();

    private List<String> availableAccounts;

    @Given("the user is logged in to Parabank")
    public void logged_in() {
        login.open();
        login.login("demo", "demo");
        Assertions.assertTrue(overview.isVisible(), "Accounts Overview not visible after login.");
        // ✅ Capture available accounts *while still on overview page*
        availableAccounts = overview.getAccountNumbers();
        System.out.println("Available accounts: " + availableAccounts);
    }

    @When("the user navigates to Transfer Funds")
    public void go_to_transfer() {
        transfer.open();
    }

    @When("transfers {int} from account {string} to account {string}")
    public void do_transfer(int amount, String fromAcc, String toAcc) {
        List<String> accounts = overview.getAccountNumbers();
        if (accounts.size() < 2) {
            overview.goToOpenNewAccount();
            openAccount.openSavingsFromFirstAccount();
            transfer.open();
        }
        transfer.transfer(String.valueOf(amount), fromAcc, toAcc);
    }

    @Then("the transfer confirmation page should show success")
    public void verify_success() {
        Assertions.assertTrue(transfer.isSuccessShown(), "Transfer success message not visible.");
    }
}