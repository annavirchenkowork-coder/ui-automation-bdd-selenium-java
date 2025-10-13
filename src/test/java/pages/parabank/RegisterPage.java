package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;
import static util.Driver.getDriver;

public class RegisterPage {
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

    public void open() {
        getDriver().get(util.ConfigurationReader.getProperty("baseUrl.parabank") + "register.htm");
        BrowserUtil.waitForVisibility(USER, 8);
    }

    public void register(String username, String password) {
        registerMinimal(username, password);
    }

    /** Used by the self-healing flow. Clears inputs, fills, submits, and waits briefly. */
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

        BrowserUtil.sleep(750);
        BrowserUtil.textContains(getDriver(), SUCCESS, "welcome", 3); // soft wait; result ignored
    }

    private void type(By locator, String value) {
        var el = getDriver().findElement(locator);
        el.clear();
        el.sendKeys(value);
    }
}