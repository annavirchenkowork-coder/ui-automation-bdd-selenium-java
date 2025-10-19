package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;
import static util.Driver.getDriver;

/**
 * Represents the Parabank registration page.
 * Handles user registration flow used in test setup and auth scenarios.
 */
public class RegisterPage {

    // Locators for all registration fields
    private static final By FIRST   = By.id("customer.firstName");
    private static final By LAST    = By.id("customer.lastName");
    private static final By ADDRESS = By.id("customer.address.street");
    private static final By CITY    = By.id("customer.address.city");
    private static final By STATE   = By.id("customer.address.state");
    private static final By ZIP     = By.id("customer.address.zipCode");
    private static final By PHONE   = By.id("customer.phoneNumber");
    private static final By SSN     = By.id("customer.ssn");
    private static final By USER    = By.id("customer.username");
    private static final By PASS    = By.id("customer.password");
    private static final By CONF    = By.id("repeatedPassword");
    private static final By BTN     = By.cssSelector("input.button[value='Register']");
    private static final By SUCCESS = By.cssSelector("#rightPanel h1.title"); // usually "Welcome"

    /** Opens the registration page and waits until the form is ready. */
    public void open() {
        getDriver().get(util.ConfigurationReader.getProperty("baseUrl.parabank") + "register.htm");
        BrowserUtil.waitForVisibility(USER, 8);
    }

    /** Registers a user using the minimal required fields. */
    public void register(String username, String password) {
        registerMinimal(username, password);
    }

    /**
     * Fills out all required fields with placeholder data and submits the form.
     * Commonly used for auto-registration during setup or recovery flows.
     */
    public void registerMinimal(String username, String password) {
        type(FIRST,   "Auto");
        type(LAST,    "User");
        type(ADDRESS, "1 Test St");
        type(CITY,    "Testville");
        type(STATE,   "CA");
        type(ZIP,     "90001");
        type(PHONE,   "5551234567");
        type(SSN,     "111-22-3333");
        type(USER,    username);
        type(PASS,    password);
        type(CONF,    password);

        getDriver().findElement(BTN).click();

        // Short wait to stabilize UI transition
        BrowserUtil.sleep(750);
        BrowserUtil.textContains(getDriver(), SUCCESS, "welcome", 3); // soft verification
    }

    /** Helper method to clear and fill a text input. */
    private void type(By locator, String value) {
        var el = getDriver().findElement(locator);
        el.clear();
        el.sendKeys(value);
    }
}