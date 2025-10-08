package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import util.BrowserUtil;

import java.util.List;
import java.util.stream.Collectors;

import static util.Driver.getDriver;

public class AccountsOverviewPage {
    // Title at the top of the right panel, e.g. "Accounts Overview"
    private static final By ROOT = By.cssSelector("h1.title");
    // Table links for each account number
    private static final By ACCOUNT_LINKS = By.cssSelector("#accountTable tbody tr td a");
    // Left-menu link
    private static final By LEFT_OPEN_NEW_ACCOUNT = By.linkText("Open New Account");

    public AccountsOverviewPage() {
    }

    /**
     * Page is considered visible when the right-panel title shows "Accounts Overview".
     */
    public boolean isVisible() {
        return BrowserUtil.isDisplayed(ROOT);
    }

    /**
     * Returns all account numbers listed in the account table.
     */
    public List<String> getAccountNumbers() {
        BrowserUtil.waitForVisibility(ACCOUNT_LINKS, 15);
        return getDriver().findElements(ACCOUNT_LINKS)
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * Clicks the left navigation link to open the "Open New Account" page.
     */
    public void goToOpenNewAccount() {
        getDriver().findElement(LEFT_OPEN_NEW_ACCOUNT).click();
    }
}