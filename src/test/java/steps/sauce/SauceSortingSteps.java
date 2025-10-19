package steps.sauce;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import pages.sauce.InventoryPage;
import util.Driver;
import static org.junit.jupiter.api.Assertions.*;

public class SauceSortingSteps {

    private final WebDriver driver = Driver.getDriver();
    private final InventoryPage inventoryPage = new InventoryPage(driver);

    private String rememberedFirstPrice;

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
        assertEquals(expected, actual, "Unexpected first price after sorting");
    }


    @Then("all visible product prices should be in non-decreasing order")
    public void prices_non_decreasing() {
        var prices = inventoryPage.allPrices();
        for (int i = 1; i < prices.size(); i++) {
            double prev = prices.get(i - 1);
            double curr = prices.get(i);
            assertTrue(curr >= prev,
                    "Found inversion at index " + i + ": " + prev + " > " + curr + " | list=" + prices);
        }
    }
    @And("the user remembers the first product price")
    public void remember_first_price() {
        rememberedFirstPrice = inventoryPage.firstProductPrice();
    }

    @Then("the first product price should not equal the remembered price")
    public void first_price_should_change() {
        assertNotEquals(rememberedFirstPrice, inventoryPage.firstProductPrice(),
                "First price did not change after switching sort option");
    }

}