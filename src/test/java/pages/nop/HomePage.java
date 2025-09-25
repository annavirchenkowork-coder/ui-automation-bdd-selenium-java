package pages.nop;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.BrowserUtil;
import util.ConfigurationReader;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object representing the nopCommerce home page.
 * Provides navigation to categories and utilities to read cart status.
 */
public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    // Locators
    private static final By CART_QTY = By.cssSelector("span.cart-qty"); // example: "(2)"

    /**
     * Creates a HomePage object bound to a WebDriver instance.
     * Base URL is loaded from configuration (conf.properties).
     */
    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));

        String raw = ConfigurationReader.getProperty("baseUrl.nop");
        this.baseUrl = (raw != null && !raw.isBlank())
                ? (raw.endsWith("/") ? raw : raw + "/")
                : "https://demo.nopcommerce.com/"; // fallback if config missing
    }

    /** Navigate to the nopCommerce home page. */
    public void open() {
        driver.get(baseUrl);
        BrowserUtil.waitForPageToLoad(5);
    }

    /**
     * Open a product category by name.
     * - "Books" navigates directly via URL (fast, avoids flaky menu clicks).
     * - Other categories are opened by clicking the link text in the menu.
     */
    public void openCategory(String name) {
        if ("Books".equalsIgnoreCase(name)) {
            driver.get(baseUrl + "books");
            BrowserUtil.waitForPageToLoad(5);
            wait.until(ExpectedConditions.urlContains("/books"));
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText(name))).click();
        }
    }

    /**
     * Returns the raw cart badge text, e.g. "(2)".
     * If the badge is not present, returns "(0)".
     */
    public String cartBadgeText() {
        String raw = BrowserUtil.safeGetText(driver, CART_QTY);
        return raw.isEmpty() ? "(0)" : raw.trim();
    }

    /**
     * Returns the numeric cart count parsed from the badge text.
     * Example: "(2)" -> 2.
     */
    public int cartBadgeCount() {
        String t = cartBadgeText();
        Matcher m = Pattern.compile("\\((\\d+)\\)").matcher(t);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }
}