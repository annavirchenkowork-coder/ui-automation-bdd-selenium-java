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
    private static final By SUCCESS = By.cssSelector("#rightPanel h1.title");

    public void open() {
        getDriver().get(util.ConfigurationReader.getProperty("baseUrl.parabank") + "register.htm");
        BrowserUtil.waitForVisibility(USER, 8);
    }

    public void register(String username, String password) {
        getDriver().findElement(FIRST).sendKeys("Auto");
        getDriver().findElement(LAST).sendKeys("User");
        getDriver().findElement(ADDRESS).sendKeys("1 Test St");
        getDriver().findElement(CITY).sendKeys("Testville");
        getDriver().findElement(STATE).sendKeys("CA");
        getDriver().findElement(ZIP).sendKeys("90001");
        getDriver().findElement(PHONE).sendKeys("5551234567");
        getDriver().findElement(SSN).sendKeys("111-22-3333");
        getDriver().findElement(USER).sendKeys(username);
        getDriver().findElement(PASS).sendKeys(password);
        getDriver().findElement(CONF).sendKeys(password);
        getDriver().findElement(BTN).click();
        BrowserUtil.textContains(getDriver(), SUCCESS, "Welcome", 8);
    }
}