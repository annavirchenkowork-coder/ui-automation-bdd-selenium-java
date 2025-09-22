package pages.nop;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the nopCommerce Cart page.
 * Encapsulates navigation to the cart and reading cart contents.
 */
public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    // Locators
    private final By cartLink = By.cssSelector("a.ico-cart");
    private final By cartRows = By.cssSelector("table.cart tbody tr");
    private final By rowProductName = By.cssSelector(".product a");

    /**
     * Opens the cart and waits until it is fully loaded.
     */
    public void openCart() {
        driver.findElement(cartLink).click();
        wait.until(ExpectedConditions.urlContains("/cart"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartRows));
    }

    /**
     * Returns the product names currently listed in the cart.
     */
    public List<String> getItemNames() {
        return driver.findElements(cartRows).stream()
                .map(r -> r.findElement(rowProductName).getText().trim())
                .collect(Collectors.toList());
    }
}