package steps.parabank;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import pages.parabank.*;

import java.util.List;

/**
 * Transfer Funds flow.
 * Assumes Background already registered + logged the user in (see ParabankLoginSteps).
 * Here we just ensure there are at least two accounts, navigate to Transfer Funds,
 * and perform a transfer between two distinct accounts.
 */
public class ParabankTransferSteps {

    private final AccountsOverviewPage overview = new AccountsOverviewPage();
    private final OpenNewAccountPage openAccount = new OpenNewAccountPage();
    private final TransferFundsPage transfer = new TransferFundsPage();

    private List<String> availableAccounts;

    @When("the user navigates to Transfer Funds")
    public void go_to_transfer() {
        ensureTwoAccountsExist();
        transfer.open();
    }

    @When("transfers {int} from any account to a different account")
    public void transfer_any_to_different(int amount) {
        ensureTwoAccountsExist();
        String from = availableAccounts.get(0);
        String to   = availableAccounts.get(1);
        transfer.transfer(String.valueOf(amount), from, to);
    }

    @Then("the transfer confirmation page should show success")
    public void verify_success() {
        Assertions.assertTrue(transfer.isSuccessShown(), "Transfer success not detected in title or panel text.");
    }

    // --- helpers ---

    private void ensureTwoAccountsExist() {

        overview.ensureAt();
        if (availableAccounts == null || availableAccounts.size() < 2) {
            availableAccounts = overview.getAccountNumbers();

            if (availableAccounts.size() < 2) {
                overview.goToOpenNewAccount();
                openAccount.openSavingsFromFirstAccount();
                availableAccounts = overview.getAccountNumbers();
            }

            Assertions.assertTrue(
                    availableAccounts.size() >= 2,
                    "Need at least two accounts to transfer. Found: " + availableAccounts
            );
        }
    }
}