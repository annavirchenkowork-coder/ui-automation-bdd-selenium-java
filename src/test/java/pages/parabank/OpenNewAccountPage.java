package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import util.BrowserUtil;

import static util.Driver.getDriver;

/**
 * Represents the Parabank "Open New Account" page.
 * Handles the process of opening new accounts via UI interactions.
 */
public class OpenNewAccountPage {

    // Form elements
    private static final By TYPE = By.id("type");
    private static final By FROM = By.id("fromAccountId");
    private static final By BTN_OPEN = By.cssSelector("input.button");
    private static final By NEW_ACCOUNT_ID = By.id("newAccountId");

    /**
     * Opens a new SAVINGS account using the first available existing account as the source.
     * Waits for confirmation and returns the newly created account ID.
     */
    public String openSavingsFromFirstAccount() {
        BrowserUtil.waitForVisibility(TYPE, 8);
        new Select(getDriver().findElement(TYPE)).selectByVisibleText("SAVINGS");
        new Select(getDriver().findElement(FROM)).selectByIndex(0);
        getDriver().findElement(BTN_OPEN).click();
        BrowserUtil.waitForVisibility(NEW_ACCOUNT_ID, 8);
        return getDriver().findElement(NEW_ACCOUNT_ID).getText().trim();
    }
}