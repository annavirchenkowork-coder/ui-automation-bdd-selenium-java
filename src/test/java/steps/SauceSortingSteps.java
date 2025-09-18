package steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import pages.sauce.InventoryPage;
import util.Driver;

public class SauceSortingSteps {

    private final WebDriver driver = Driver.getDriver();
    private final InventoryPage inventoryPage = new InventoryPage(driver);

    @When("the user sorts products by {string}")
    public void the_user_sorts_products_by(String option) {
        inventoryPage.selectSortBy(option);
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    @Then("the first product price should be {string}")
    public void the_first_product_price_should_be(String expected) {
        long end = System.currentTimeMillis() + 3000;
        String actual = "";
        while (System.currentTimeMillis() < end) {
            actual = inventoryPage.firstProductPrice();
            if (expected.equals(actual)) break;
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
        Assertions.assertEquals(expected, actual, "Unexpected first price after sorting");
    }
}