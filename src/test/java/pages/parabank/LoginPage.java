package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.BrowserUtil;

public class LoginPage {
    private final WebDriver driver;

    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By BTN_LOGIN = By.cssSelector("input.button");

    private static final By ERROR_MSG = By.cssSelector("p.error");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        BrowserUtil.openPage("baseUrl.parabank", "parabank/index.htm");
        BrowserUtil.waitForVisibility(USERNAME, 8);
    }


}