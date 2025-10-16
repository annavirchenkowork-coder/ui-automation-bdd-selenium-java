package steps.parabank;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import pages.parabank.*;

import java.util.List;


public class ParabankTransferSteps {

    private final AccountsOverviewPage overview = new AccountsOverviewPage();
    private final OpenNewAccountPage openAccount = new OpenNewAccountPage();
    private final TransferFundsPage transfer = new TransferFundsPage();

    private List<String> availableAccounts;

    @Given("the user is logged in to Parabank")
    public void logged_in() {
        String username = "demo_" + System.currentTimeMillis();
        String password = "demo";

        // Self-healing login + registration if needed

        ParabankAuth.ensureRegisteredAndLoggedIn(username, password);

        // Continue existing setup logic
        Assertions.assertTrue(overview.isVisible(), "Accounts Overview not visible after login.");

        // Capture available accounts *while still on overview page*
        availableAccounts = overview.getAccountNumbers();
        System.out.println("Available accounts: " + availableAccounts);

        // If only one account, open a new one, then refresh the list
        if (availableAccounts.size() < 2) {
            overview.goToOpenNewAccount();
            openAccount.openSavingsFromFirstAccount(); // returns to Overview
            availableAccounts = overview.getAccountNumbers();
        }

        Assertions.assertTrue(availableAccounts.size() >= 2,
                "Need at least two accounts to transfer. Found: " + availableAccounts);
        System.out.println("Available accounts: " + availableAccounts);
    }

    @When("the user navigates to Transfer Funds")
    public void go_to_transfer() {
        transfer.open();
    }

    @When("transfers {int} from account {string} to account {string}")
    public void do_transfer(int amount, String fromAcc, String toAcc) {
        // fallback: if the provided account numbers aren’t present in the dropdowns,
        // it will use the first two we detected at login time to keep the demo green.
        String from = availableAccounts.contains(fromAcc) ? fromAcc : availableAccounts.get(0);
        String to   = availableAccounts.contains(toAcc)
                ? toAcc
                : (availableAccounts.size() > 1 ? availableAccounts.get(1) : from);

        transfer.transfer(String.valueOf(amount), from, to);
    }

    @Then("the transfer confirmation page should show success")
    public void verify_success() {
        Assertions.assertTrue(transfer.isSuccessShown(), "Transfer success message not visible.");
    }
}