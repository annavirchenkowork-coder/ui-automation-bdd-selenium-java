package pages.sauce;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.util.List;
import java.util.ArrayList;

public class InventoryPage {
    private final WebDriver driver;

    public InventoryPage(WebDriver driver) { this.driver = driver; }

    // sort dropdown
    private final By sortSelect = By.cssSelector("[data-test='product-sort-container']");

    // item prices on inventory grid
    private final By itemPrices = By.cssSelector(".inventory_item_price");

    public void selectSortBy(String visibleText) {
        WebElement select = driver.findElement(sortSelect);
        new Select(select).selectByVisibleText(visibleText);
    }

    public String firstProductPrice() {
        List<WebElement> prices = driver.findElements(itemPrices);
        if (prices.isEmpty()) return "";
        return prices.get(0).getText().trim();
    }

    public List<Double> allPrices() {
        List<Double> values = new ArrayList<>();
        for (WebElement el : driver.findElements(itemPrices)) {
            String txt = el.getText().replace("$", "").trim();
            if (!txt.isEmpty()) {
                values.add(Double.parseDouble(txt));
            }
        }
        return values;
    }
}