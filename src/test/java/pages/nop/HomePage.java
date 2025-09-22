package pages.nop;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.ConfigurationReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public int cartBadgeCount() {
        String t = cartBadgeText(); // "(2)" or "(0)"
        Matcher m = Pattern.compile("\\((\\d+)\\)").matcher(t);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }
}