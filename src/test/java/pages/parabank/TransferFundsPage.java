package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import util.BrowserUtil;

import java.util.List;

import static util.Driver.getDriver;

public class TransferFundsPage {
    private static final By AMOUNT = By.id("amount");
    private static final By FROM = By.id("fromAccountId");
    private static final By TO = By.id("toAccountId");
    private static final By BTN_TRANSFER = By.cssSelector("input.button");
    private static final By TITLE = By.cssSelector("#rightPanel .title");

    public void open() {
        BrowserUtil.openPage("baseUrl.parabank", "transfer.htm");
        BrowserUtil.waitForVisibility(AMOUNT, 10);
    }

    public void transfer(String amount, String fromAccount, String toAccount) {
        // wait until amount field is visible before typing
        BrowserUtil.waitForVisibility(AMOUNT, 10);

        WebElement amountEl = getDriver().findElement(AMOUNT);
        amountEl.clear();
        amountEl.sendKeys(amount);

        WebElement fromEl = getDriver().findElement(FROM);
        WebElement toEl   = getDriver().findElement(TO);

        List<String> fromOptions = BrowserUtil.getAllSelectOptions(fromEl);
        List<String> toOptions   = BrowserUtil.getAllSelectOptions(toEl);

        if (fromOptions.contains(fromAccount)) {
            BrowserUtil.selectByVisibleText(fromEl, fromAccount);
        } else if (!fromOptions.isEmpty()) {
            BrowserUtil.selectByVisibleText(fromEl, fromOptions.get(0));
        }

        if (toOptions.contains(toAccount)) {
            BrowserUtil.selectByVisibleText(toEl, toAccount);
        } else if (toOptions.size() > 1) {
            BrowserUtil.selectByVisibleText(toEl, toOptions.get(1));
        }

        getDriver().findElement(BTN_TRANSFER).click();
    }

    public boolean isSuccessShown() {
        return BrowserUtil.textContains(getDriver(), TITLE, "Transfer Complete!", 10);
    }
}