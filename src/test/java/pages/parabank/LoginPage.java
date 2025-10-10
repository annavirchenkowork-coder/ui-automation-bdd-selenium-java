package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;
import util.Driver;

public class LoginPage {

    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By BTN_LOGIN = By.cssSelector("input.button");

    public LoginPage() {

    }

    public void open() {
        BrowserUtil.openPage("baseUrl.parabank", "index.htm");
        BrowserUtil.waitForVisibility(USERNAME, 8);
        if (BrowserUtil.safeGetText(Driver.getDriver(), By.cssSelector("#rightPanel")).contains("An internal error has occurred")) {
            throw new IllegalStateException("Parabank shows server error page. Aborting test early.");
        }
    }

    public void login(String user, String pass) {
        BrowserUtil.performLogin(USERNAME, PASSWORD, BTN_LOGIN, user, pass);
    }

    public boolean isVisible() {
        return BrowserUtil.isDisplayed(USERNAME);
    }


}