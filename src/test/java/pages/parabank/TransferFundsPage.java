package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;

import static util.Driver.getDriver;

public class TransferFundsPage {
    private static final By AMOUNT = By.id("amount");
    private static final By FROM = By.id("fromAccountId");
    private static final By TO = By.id("toAccountId");
    private static final By BTN_TRANSFER = By.cssSelector("input.button");
    private static final By SUCCESS_TITLE = By.cssSelector("#rightPanel .title");

    public void open() {
        getDriver().get(util.ConfigurationReader.getProperty("baseUrl.parabank") + "transfer.htm");
        BrowserUtil.waitForVisibility(AMOUNT, 8);
    }

    public void transfer(String amount, String fromAccount, String toAccount) {
        getDriver().findElement(AMOUNT).clear();
        getDriver().findElement(AMOUNT).sendKeys(amount);

        BrowserUtil.selectByVisibleText(getDriver().findElement(FROM), fromAccount);
        BrowserUtil.selectByVisibleText(getDriver().findElement(TO), toAccount);

        getDriver().findElement(BTN_TRANSFER).click();
    }

    public boolean isSuccessShown() {
        return BrowserUtil.textContains(getDriver(), SUCCESS_TITLE, "Transfer Complete!", 8);
    }

}