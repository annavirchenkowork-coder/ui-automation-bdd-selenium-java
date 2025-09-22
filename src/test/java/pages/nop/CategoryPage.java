package pages.nop;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class CategoryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait  = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    // Finds a product card by its display name
    private By productCardByName(String name) {
        return By.xpath("//h2[@class='product-title']/a[normalize-space(.)='" + name + "']" +
                "/ancestor::div[contains(@class,'product-item')]");
    }

    // Button inside a product card
    private static final By ADD_TO_CART_BTN_WITHIN =
            By.cssSelector("button.button-2.product-box-add-to-cart-button");

    // Success notification and its close button
    private static final By SUCCESS_BAR =
            By.cssSelector("div.bar-notification.success");
    private static final By SUCCESS_BAR_CLOSE =
            By.cssSelector("div.bar-notification.success span.close");

    // Add a product to the cart by name
    public void addProductByName(String name) {
        WebElement card = wait.until(ExpectedConditions
                .visibilityOfElementLocated(productCardByName(name)));
        WebElement addBtn = card.findElement(ADD_TO_CART_BTN_WITHIN);

        clickResilient(addBtn, productCardByName(name), ADD_TO_CART_BTN_WITHIN);

        wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_BAR));
        try {
            driver.findElement(SUCCESS_BAR_CLOSE).click();
        } catch (Exception ignored) {}
        wait.until(ExpectedConditions.invisibilityOfElementLocated(SUCCESS_BAR));
    }

    // Retry once if click is intercepted or stale
    private void clickResilient(WebElement element,
                                By cardLocator,
                                By innerButtonLocator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
            WebElement refreshedBtn = wait.until(ExpectedConditions
                    .elementToBeClickable(
                            driver.findElement(cardLocator).findElement(innerButtonLocator)));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", refreshedBtn);
            refreshedBtn.click();
        }
    }
}