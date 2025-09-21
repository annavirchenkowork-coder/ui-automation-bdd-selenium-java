package pages.nop;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.ConfigurationReader;

public class HomePage {
    private final WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    private final By cartQty = By.cssSelector("span.cart-qty");

    public void open(String baseUrl) {
        driver.get(baseUrl);
    }

    public void openCategory(String name) {
        String baseUrl = ConfigurationReader.getProperty("baseUrl.nop");
        if ("Books".equalsIgnoreCase(name)) {
            driver.get(baseUrl + "books");
        } else {
            driver.findElement(By.linkText(name)).click();
        }
    }

    public String cartBadgeText() {
        return driver.findElement(cartQty).getText().trim();
    }
}