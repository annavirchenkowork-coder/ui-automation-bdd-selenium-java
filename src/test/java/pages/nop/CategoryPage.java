package pages.nop;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class CategoryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    private By productCardByName(String name) {
        return By.xpath(
                "//h2[@class='product-title']/a[normalize-space(.)='" + name + "']" +
                        "/ancestor::div[contains(@class,'product-item')]"
        );
    }
    private final By addToCartBtnWithin = By.cssSelector("button.button-2.product-box-add-to-cart-button");
    private final By successBar = By.cssSelector("div.bar-notification.success");
    private final By successBarClose = By.cssSelector("div.bar-notification.success span.close");

    private By productTiles() {
        return By.cssSelector(".product-item");
    }

    public void addProductByName(String name) {
        WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(productCardByName(name)));
        WebElement btn = card.findElement(addToCartBtnWithin);

        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();

        // Wait for success bar, then close and wait it away,
        // so the next click isn’t intercepted.
        wait.until(ExpectedConditions.visibilityOfElementLocated(successBar));
        try { driver.findElement(successBarClose).click(); } catch (Exception ignored) {}
        wait.until(ExpectedConditions.invisibilityOfElementLocated(successBar));
    }
}