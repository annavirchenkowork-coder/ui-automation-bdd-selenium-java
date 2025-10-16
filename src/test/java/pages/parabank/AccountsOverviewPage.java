package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import util.BrowserUtil;

import java.util.List;

import static util.Driver.getDriver;

public class AccountsOverviewPage {
    // Right-panel title (e.g., "Accounts Overview")
    private static final By ROOT = By.cssSelector("#rightPanel h1.title");
    // Table links for each account number
    private static final By ACCOUNT_LINKS = By.cssSelector("#accountTable tbody tr td a");
    // Left-menu links
    private static final By LEFT_OPEN_NEW_ACCOUNT = By.linkText("Open New Account");
    private static final By LEFT_ACCOUNTS_OVERVIEW = By.linkText("Accounts Overview");

    public AccountsOverviewPage() {}

    public boolean isVisible() {
        return BrowserUtil.textContains(getDriver(), ROOT, "Accounts Overview", 2)
                || getDriver().getTitle().contains("Accounts Overview")
                || getDriver().getCurrentUrl().contains("overview.htm");
    }

    /** Ensure we are on Accounts Overview; if not, navigate there and wait for table. */
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

    /** Returns all account numbers listed in the account table. */
    public List<String> getAccountNumbers() {
        ensureAt();
        return getDriver().findElements(ACCOUNT_LINKS)
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
    }

    /** Clicks the left navigation link to open the "Open New Account" page. */
    public void goToOpenNewAccount() {
        getDriver().findElement(LEFT_OPEN_NEW_ACCOUNT).click();
    }
}