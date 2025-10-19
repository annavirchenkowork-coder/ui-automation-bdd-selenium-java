package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;

import static util.Driver.getDriver;

/**
 * Represents the Parabank login page and its main interactions.
 * Encapsulates navigation, login flow, and basic visibility checks.
 */
public class LoginPage {

    // Locators for login elements
    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By BTN_LOGIN = By.cssSelector("input.button");
    private static final By ERROR_BOX = By.cssSelector("#rightPanel .error");
    private static final By REGISTER_LINK = By.linkText("Register");

    public LoginPage() {
    }

    /** Opens the Parabank login page and validates it’s in a usable state. */
    public void open() {
        BrowserUtil.openPage("baseUrl.parabank", "index.htm");

        // Wait until either the login form or a logged-in state is visible
        try {
            BrowserUtil.waitForVisibility(USERNAME, 8);
        } catch (org.openqa.selenium.TimeoutException e) {
            // Handle case where user might already be logged in
            if (!BrowserUtil.isDisplayed(By.linkText("Log Out"))) {
                throw e; // Neither login form nor Log Out → real page load issue
            }
        }

        // Defensive check for backend/server error page
        if (BrowserUtil.safeGetText(getDriver(), By.cssSelector("#rightPanel"))
                .contains("An internal error has occurred")) {
            throw new IllegalStateException("Parabank shows server error page. Aborting test early.");
        }
    }

    /** Logs in using provided credentials. */
    public void login(String user, String pass) {
        BrowserUtil.performLogin(USERNAME, PASSWORD, BTN_LOGIN, user, pass);
    }

    /** Returns true if the login form is visible on the page. */
    public boolean isVisible() {
        return BrowserUtil.isDisplayed(USERNAME);
    }

    /** Retrieves any error message displayed under the login form. */
    public String getErrorMessage() {
        return BrowserUtil.safeGetText(getDriver(), ERROR_BOX).toLowerCase();
    }

    /** Navigates from login page to registration form. */
    public void goToRegister() {
        getDriver().findElement(REGISTER_LINK).click();
    }
}