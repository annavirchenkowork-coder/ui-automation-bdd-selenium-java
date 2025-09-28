package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;

public class AccountsOverviewPage {
    private static final By ROOT = By.cssSelector("h1.title");

    public AccountsOverviewPage() {
    }

    public boolean isVisible() {
        return BrowserUtil.isDisplayed(ROOT);
    }
}