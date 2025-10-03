package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import util.BrowserUtil;

import static util.Driver.getDriver;

public class OpenNewAccountPage {
    private static final By TYPE = By.id("type");
    private static final By FROM = By.id("fromAccountId");
    private static final By BTN_OPEN = By.cssSelector("input.button");
    private static final By NEW_ACCOUNT_ID = By.id("newAccountId");

    public String openSavingsFromFirstAccount() {
        BrowserUtil.waitForVisibility(TYPE, 8);
        new Select(getDriver().findElement(TYPE)).selectByVisibleText("SAVINGS");
        new Select(getDriver().findElement(FROM)).selectByIndex(0);
        getDriver().findElement(BTN_OPEN).click();
        BrowserUtil.waitForVisibility(NEW_ACCOUNT_ID, 8);
        return getDriver().findElement(NEW_ACCOUNT_ID).getText().trim();
    }
}