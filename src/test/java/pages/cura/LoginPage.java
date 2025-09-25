package pages.cura;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.BrowserUtil;

public class LoginPage {
    private final WebDriver driver;

    // Locators
    private static final By FLD_USERNAME = By.id("txt-username");
    private static final By FLD_PASSWORD = By.id("txt-password");
    private static final By BTN_LOGIN    = By.id("btn-login");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String username, String password) {
        // Wait for username field to be visible before interacting
        BrowserUtil.waitForVisibility(FLD_USERNAME, 8);

        driver.findElement(FLD_USERNAME).clear();
        driver.findElement(FLD_USERNAME).sendKeys(username);

        driver.findElement(FLD_PASSWORD).clear();
        driver.findElement(FLD_PASSWORD).sendKeys(password);

        driver.findElement(BTN_LOGIN).click();
    }
}