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
    private static final By REGISTER_LINK = By.linkText("Register");

    public LoginPage() {}

    public void open() {
        BrowserUtil.openPage("baseUrl.parabank", "index.htm");

        // Either the login field is visible OR we see Log Out (already logged in)
        try {
            BrowserUtil.waitForVisibility(USERNAME, 8);
        } catch (org.openqa.selenium.TimeoutException e) {
            if (!BrowserUtil.isDisplayed(By.linkText("Log Out"))) {
                throw e; // neither login nor logged-in state → real problem
            }
        }

        if (BrowserUtil.safeGetText(getDriver(), By.cssSelector("#rightPanel"))
                .contains("An internal error has occurred")) {
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

    public String getErrorMessage() {
        return BrowserUtil.safeGetText(getDriver(), ERROR_BOX).toLowerCase();
    }

    public void goToRegister() {
        getDriver().findElement(REGISTER_LINK).click();
    }
}