package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;

import static util.Driver.getDriver;

public class LoginPage {

    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By BTN_LOGIN = By.cssSelector("input.button");
    private static final By ERROR_BOX = By.cssSelector("#rightPanel .error");
    private static final By H1_TITLE = By.cssSelector("#rightPanel h1.title");

    public LoginPage() {}

    public void open() {
        BrowserUtil.openPage("baseUrl.parabank", "index.htm");
        BrowserUtil.waitForVisibility(USERNAME, 8);
        if (BrowserUtil.safeGetText(getDriver(), By.cssSelector("#rightPanel")).contains("An internal error has occurred")) {
            throw new IllegalStateException("Parabank shows server error page. Aborting test early.");
        }
    }

    public void login(String user, String pass) {
        BrowserUtil.performLogin(USERNAME, PASSWORD, BTN_LOGIN, user, pass);
    }

    public boolean isVisible() {
        return BrowserUtil.isDisplayed(USERNAME);
    }

    /** True when the “invalid creds” error is shown. */
    public boolean invalidCredsShown() {
        return BrowserUtil.textContains(getDriver(), ERROR_BOX,
                "The username and password could not be verified.", 4);
    }

    /** True when we land on any account page (title exists). */
    public boolean loggedIn() {
        return BrowserUtil.textContains(getDriver(), H1_TITLE, "Accounts", 6);
    }
}