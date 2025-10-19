package steps.parabank;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import pages.parabank.*;

import java.util.List;

/**
 * Cucumber steps for the Transfer Funds flow.
 * Preconditions: user is already authenticated (handled in Background).
 * Responsibility: ensure at least two accounts exist, navigate, execute transfer, verify outcome.
 */
public class ParabankTransferSteps {

    private final AccountsOverviewPage overview = new AccountsOverviewPage();
    private final OpenNewAccountPage openAccount = new OpenNewAccountPage();
    private final TransferFundsPage transfer = new TransferFundsPage();

    // Cached set of account numbers discovered/created during the scenario
    private List<String> availableAccounts;

    @When("the user navigates to Transfer Funds")
    public void go_to_transfer() {
        // Guarantee precondition for transfers (>= 2 accounts) before navigation
        ensureTwoAccountsExist();
        transfer.open();
    }

    @When("transfers {int} from any account to a different account")
    public void transfer_any_to_different(int amount) {
        // Make sure we have distinct source/target accounts
        ensureTwoAccountsExist();
        String from = availableAccounts.get(0);
        String to   = availableAccounts.get(1);
        transfer.transfer(String.valueOf(amount), from, to);
    }

    @Then("the transfer confirmation page should show success")
    public void verify_success() {
        // Accept either title or panel text as the success signal
        Assertions.assertTrue(transfer.isSuccessShown(), "Transfer success not detected in title or panel text.");
    }

    // --- helpers ---

    /**
     * Ensures there are at least two accounts available for transfer.
     * Creates a new Savings account if only one exists, then refreshes the cache.
     */
    private void ensureTwoAccountsExist() {

        overview.ensureAt();

        // Lazily populate or refresh the cached account list
        if (availableAccounts == null || availableAccounts.size() < 2) {
            availableAccounts = overview.getAccountNumbers();

            // Create a second account when needed (uses default first account as funding)
            if (availableAccounts.size() < 2) {
                overview.goToOpenNewAccount();
                openAccount.openSavingsFromFirstAccount();
                availableAccounts = overview.getAccountNumbers();
            }

            // Hard assertion: transfers require at least two accounts
            Assertions.assertTrue(
                    availableAccounts.size() >= 2,
                    "Need at least two accounts to transfer. Found: " + availableAccounts
            );
        }
    }
}