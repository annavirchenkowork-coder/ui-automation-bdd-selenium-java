package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;

public class AccountsOverviewPage {
    private static final By ROOT = By.cssSelector("h1.title");
    private static final By ACCOUNT_LINKS = By.cssSelector("#accountTable tbody tr td a");
    private static final By LEFT_OPEN_NEW_ACCOUNT = By.linkText("Open New Account");

    public AccountsOverviewPage() {
    }

    public boolean isVisible() {
        return BrowserUtil.isDisplayed(ROOT);
    }
}