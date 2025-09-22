package steps;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

import pages.nop.HomePage;
import pages.nop.CategoryPage;
import pages.nop.CartPage;
import util.Driver;

public class NopCartSteps {
    // Single driver instance from utility
    private final WebDriver driver = Driver.getDriver();

    // Page objects for nopCommerce
    private final HomePage home = new HomePage(driver);
    private final CategoryPage category = new CategoryPage(driver);
    private final CartPage cart = new CartPage(driver);

    @Given("the user is on the nopCommerce home page")
    public void on_home() {
        home.open(); // baseUrl from config

        // confirm page title contains "nopCommerce"
        new WebDriverWait(driver, Duration.ofSeconds(6)).until(d -> driver.getTitle().toLowerCase().contains("nopcommerce"));
    }

    @When("the user opens the {string} category")
    public void open_category(String name) {
        home.openCategory(name);
    }

    @When("the user adds products {string} and {string} to the cart")
    public void add_two(String p1, String p2) {
        int start = home.cartBadgeCount();

        // add first product, wait until count updates
        category.addProductByName(p1);
        waitForCartCount(start + 1);

        // add second product, wait until count updates
        category.addProductByName(p2);
        waitForCartCount(start + 2);
    }

    // small helper for badge synchronization
    private void waitForCartCount(int expected) {
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(d -> home.cartBadgeCount() == expected);
    }

    @Then("the cart badge should show {string}")
    public void badge_should_show(String count) {
        Assertions.assertEquals(Integer.parseInt(count), home.cartBadgeCount(), "Cart badge mismatch");
    }

    @When("the user opens the cart")
    public void open_cart() {
        cart.openCart();
    }

    @Then("the cart should list items:")
    public void cart_should_list_items(List<String> expected) {
        List<String> actual = cart.getItemNames();
        for (String e : expected) {
            Assertions.assertTrue(actual.contains(e), "Missing item: " + e + " in " + actual);
        }
    }
}