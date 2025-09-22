package pages.nop;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    private final By cartLink = By.cssSelector("a.ico-cart");
    private final By cartRows = By.cssSelector("table.cart tbody tr");
    private final By rowProductName = By.cssSelector(".product a");

    public void openCart() {
        driver.findElement(cartLink).click();
        wait.until(ExpectedConditions.urlContains("/cart"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartRows));
    }

    public List<String> getItemNames() {
        return driver.findElements(cartRows).stream()
            .map(r -> r.findElement(rowProductName).getText().trim())
            .collect(Collectors.toList());
    }
}