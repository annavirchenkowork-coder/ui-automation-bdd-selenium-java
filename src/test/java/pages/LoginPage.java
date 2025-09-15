package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private final WebDriver driver;
    private final By user = By.id("user-name");
    private final By pass = By.id("password");
    private final By loginBtn = By.id("login-button");
    private final By productsTitle = By.cssSelector(".title");

    public LoginPage(WebDriver driver) { this.driver = driver; }

    public void open() { driver.get("https://www.saucedemo.com/"); }

    public void login(String u, String p) {
        driver.findElement(user).sendKeys(u);
        driver.findElement(pass).sendKeys(p);
        driver.findElement(loginBtn).click();
    }

    public boolean isProductsVisible() {
        return driver.findElements(productsTitle).size() > 0;
    }
}