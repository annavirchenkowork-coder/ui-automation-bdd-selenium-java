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

    private By productTiles() { return By.cssSelector(".product-item"); }

    public void addProductByName(String name) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(productTiles()));
        List<WebElement> tiles = driver.findElements(productTiles());
        for (WebElement t : tiles) {
            String title = t.findElement(By.cssSelector("h2.product-title a")).getText().trim();
            if (title.equalsIgnoreCase(name)) {
                WebElement addBtn = t.findElement(By.cssSelector("button.product-box-add-to-cart-button"));
                ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'})", addBtn);
                wait.until(ExpectedConditions.elementToBeClickable(addBtn)).click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("bar-notification")));
                return;
            }
        }
        throw new NoSuchElementException("Product not found in category: " + name);
    }
}