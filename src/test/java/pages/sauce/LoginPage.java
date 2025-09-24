package pages.sauce;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.BrowserUtil;

public class LoginPage {
    private final WebDriver driver;

    public LoginPage(WebDriver driver) { this.driver = driver; }

    // Locators
    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginBtn = By.id("login-button");
    private final By productsTitle = By.cssSelector(".title"); // text: "Products"
    private final By errorMsg = By.cssSelector("[data-test='error']");

    // Actions
    public void open(String url) { driver.get(url); }
    public void enterUsername(String user) { driver.findElement(username).sendKeys(user); }
    public void enterPassword(String pass) { driver.findElement(password).sendKeys(pass); }
    public void clickLogin() { driver.findElement(loginBtn).click(); }

    // Assertions helpers
    public boolean isProductsPage() {
        WebElement title = driver.findElement(productsTitle);
        return title.isDisplayed() && "Products".equalsIgnoreCase(title.getText().trim());
    }

    public String getErrorText() {
        return BrowserUtil.safeGetText(driver, errorMsg);
    }
}

