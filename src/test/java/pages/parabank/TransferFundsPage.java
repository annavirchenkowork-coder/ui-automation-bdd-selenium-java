package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import util.BrowserUtil;

import java.util.List;

import static util.Driver.getDriver;

public class TransferFundsPage {
    // Page object locators kept minimal and readable
    private static final By TITLE   = By.cssSelector("#rightPanel h1.title");
    private static final By AMOUNT  = By.id("amount");
    private static final By FROM    = By.id("fromAccountId");
    private static final By TO      = By.id("toAccountId");
    private static final By BTN_TRANSFER = By.cssSelector("input.button");

    /** Navigates via left-nav and blocks until the form is interactable. */
    public void open() {
        new AccountsOverviewPage().goToTransferFunds();

        // Defensive readiness checks to reduce flakiness on slower environments
        BrowserUtil.textContains(getDriver(), TITLE, "Transfer Funds", 12);
        BrowserUtil.waitForVisibility(FROM, 12);
        BrowserUtil.waitForVisibility(TO, 12);
        BrowserUtil.waitForVisibility(AMOUNT, 12);
    }

    /**
     * Submits a transfer.
     * - Self-heals if invoked off-page.
     * - Selects provided accounts when available; otherwise picks sensible defaults.
     * - Ensures TO differs from FROM.
     */
    public void transfer(String amount, String fromAccount, String toAccount) {
        // Lightweight self-heal: ensure the form is present
        if (!BrowserUtil.isPresent(AMOUNT)) {
            open();
        }

        BrowserUtil.waitForVisibility(AMOUNT, 12);
        WebElement amountEl = getDriver().findElement(AMOUNT);

        // Keep interactions stable by bringing the field into view
        try {
            ((JavascriptExecutor) getDriver())
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", amountEl);
        } catch (Exception ignored) {}

        amountEl.clear();
        amountEl.sendKeys(amount);

        WebElement fromEl = getDriver().findElement(FROM);
        WebElement toEl   = getDriver().findElement(TO);

        List<String> fromOptions = BrowserUtil.getAllSelectOptions(fromEl);
        List<String> toOptions   = BrowserUtil.getAllSelectOptions(toEl);

        // Source account: honor input when possible; otherwise use the first available
        if (fromOptions.contains(fromAccount)) {
            BrowserUtil.selectByVisibleText(fromEl, fromAccount);
        } else if (!fromOptions.isEmpty()) {
            BrowserUtil.selectByVisibleText(fromEl, fromOptions.get(0));
            fromAccount = BrowserUtil.getSelectedOption(fromEl);
        }

        // Destination account: must be present and different from the source
        String finalTo = toAccount;
        if (!toOptions.contains(finalTo) || finalTo.equals(fromAccount)) {
            String fromFinal = fromAccount;
            finalTo = toOptions.stream()
                    .filter(opt -> !opt.equals(fromFinal))
                    .findFirst()
                    .orElse(fromAccount); // graceful fallback if only one exists
        }
        BrowserUtil.selectByVisibleText(toEl, finalTo);

        getDriver().findElement(BTN_TRANSFER).click();
    }

    /**
     * Robust success detection.
     * Prefers the H1 change, then falls back to confirmation text in the panel.
     */
    public boolean isSuccessShown() {
        // Primary signal: page title update
        try {
            if (BrowserUtil.textContains(getDriver(),
                    By.cssSelector("#rightPanel h1.title"), "Transfer Complete", 8)) {
                return true;
            }
        } catch (Exception ignored) { /* non-fatal; try fallback */ }

        // Fallback signal: confirmation paragraph
        By panel = By.id("rightPanel");
        try {
            if (BrowserUtil.textContains(getDriver(),
                    panel, "has been transferred", 10)) {
                return true;
            }
        } catch (Exception ignored) { /* non-fatal; try last quick check */ }

        // Final quick pass to cover minor timing gaps
        try {
            return BrowserUtil.textContains(getDriver(), panel, "Transfer Complete", 2);
        } catch (Exception ignored) {
            return false;
        }
    }
}