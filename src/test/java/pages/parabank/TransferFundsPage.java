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
    private static final By SUCCESS_TITLE = By.cssSelector("#rightPanel .title");

    public void open() {
        getDriver().get(util.ConfigurationReader.getProperty("baseUrl.parabank") + "transfer.htm");
        BrowserUtil.waitForVisibility(AMOUNT, 8);
    }

    public void transfer(String amount, String fromAccount, String toAccount) {
        // amount
        WebElement amountEl = getDriver().findElement(AMOUNT);
        amountEl.clear();
        amountEl.sendKeys(amount);

        // selects (use your BrowserUtil helpers)
        WebElement fromEl = getDriver().findElement(FROM);
        WebElement toEl   = getDriver().findElement(TO);

        List<String> fromOptions = BrowserUtil.getAllSelectOptions(fromEl);
        List<String> toOptions   = BrowserUtil.getAllSelectOptions(toEl);

        // Choose FROM: use requested if present, otherwise first option
        if (fromOptions.contains(fromAccount)) {
            BrowserUtil.selectByVisibleText(fromEl, fromAccount);
        } else if (!fromOptions.isEmpty()) {
            BrowserUtil.selectByVisibleText(fromEl, fromOptions.get(0));
            fromAccount = BrowserUtil.getSelectedOption(fromEl);
        }

        // Choose TO: prefer requested, but ensure it's different from FROM; otherwise pick a different one
        String finalTo = toAccount;
        if (!toOptions.contains(finalTo) || finalTo.equals(fromAccount)) {
            String finalFromAccount = fromAccount;
            finalTo = toOptions.stream()
                    .filter((String opt) -> !opt.equals(finalFromAccount))
                    .findFirst()
                    .orElse(fromAccount);
        }
        BrowserUtil.selectByVisibleText(toEl, finalTo);
        // submit
        getDriver().findElement(BTN_TRANSFER).click();
    }

    public boolean isSuccessShown() {
        return BrowserUtil.textContains(getDriver(), SUCCESS_TITLE, "Transfer Complete!", 10);
    }

}