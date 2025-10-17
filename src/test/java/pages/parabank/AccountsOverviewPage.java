package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import util.BrowserUtil;

import java.util.List;

import static util.Driver.getDriver;

/**
 * Represents the Parabank "Accounts Overview" page.
 * Provides methods to verify visibility and interact with listed accounts.
 */
public class AccountsOverviewPage {

    // Right-panel header (e.g., "Accounts Overview")
    private static final By ROOT = By.cssSelector("#rightPanel h1.title");
    // Account links inside the table
    private static final By ACCOUNT_LINKS = By.cssSelector("#accountTable tbody tr td a");
    // Left navigation links
    private static final By LEFT_OPEN_NEW_ACCOUNT = By.linkText("Open New Account");
    private static final By LEFT_ACCOUNTS_OVERVIEW = By.linkText("Accounts Overview");

    public AccountsOverviewPage() {}

    /**
     * Confirms if the user is currently on the Accounts Overview page.
     * Uses flexible checks for title, text, or URL.
     */
    public boolean isVisible() {
        return BrowserUtil.textContains(getDriver(), ROOT, "Accounts Overview", 2)
                || getDriver().getTitle().contains("Accounts Overview")
                || getDriver().getCurrentUrl().contains("overview.htm");
    }

    /**
     * Navigates to the Accounts Overview page if not already there.
     * Waits until account table elements are visible.
     */
    public void ensureAt() {
        if (!isVisible()) {
            try {
                getDriver().findElement(LEFT_ACCOUNTS_OVERVIEW).click();
            } catch (Exception ignore) {
                BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
            }
        }
        BrowserUtil.waitForVisibility(ACCOUNT_LINKS, 12);
    }

    /**
     * Retrieves all account numbers displayed in the overview table.
     * Ensures the page is open before collecting them.
     */
    public List<String> getAccountNumbers() {
        ensureAt();
        return getDriver().findElements(ACCOUNT_LINKS)
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
    }

    /**
     * Opens the "Open New Account" page via the left navigation menu.
     */
    public void goToOpenNewAccount() {
        getDriver().findElement(LEFT_OPEN_NEW_ACCOUNT).click();
    }
}