package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.BrowserUtil;

public class AccountsOverviewPage {
    private final WebDriver driver;
    private static final By ROOT = By.cssSelector("h1.title");

    public AccountsOverviewPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isVisible() {
        return BrowserUtil.isDisplayed(ROOT);
    }
}