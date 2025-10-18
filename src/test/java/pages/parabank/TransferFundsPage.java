package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import util.BrowserUtil;

import java.util.List;

import static util.Driver.getDriver;

public class TransferFundsPage {
    private static final By TITLE   = By.cssSelector("#rightPanel h1.title");
    private static final By AMOUNT  = By.id("amount");
    private static final By FROM    = By.id("fromAccountId");
    private static final By TO      = By.id("toAccountId");
    private static final By BTN_TRANSFER = By.cssSelector("input.button");

    /** Open via left nav from Accounts Overview and wait until the form is truly ready. */
    public void open() {
        new AccountsOverviewPage().goToTransferFunds();

        // Strong readiness checks
        BrowserUtil.textContains(getDriver(), TITLE, "Transfer Funds", 12);
        BrowserUtil.waitForVisibility(FROM, 12);
        BrowserUtil.waitForVisibility(TO, 12);
        BrowserUtil.waitForVisibility(AMOUNT, 12);
    }

    public void transfer(String amount, String fromAccount, String toAccount) {
        // If someone called transfer() directly, make sure controls exist.
        if (!BrowserUtil.isPresent(AMOUNT)) {
            // try to recover once: re-open the page via left nav
            open();
        }

        BrowserUtil.waitForVisibility(AMOUNT, 12);
        WebElement amountEl = getDriver().findElement(AMOUNT);

        // bring into view (just in case)
        try {
            ((JavascriptExecutor) getDriver())
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", amountEl);
        } catch (Exception ignored) {}

        amountEl.clear();
        amountEl.sendKeys(amount);

        WebElement fromEl = getDriver().findElement(FROM);
        WebElement toEl   = getDriver().findElement(TO);

        List<String> fromOptions = BrowserUtil.getAllSelectOptions(fromEl);
        List<String> toOptions   = BrowserUtil.getAllSelectOptions(toEl);

        // FROM
        if (fromOptions.contains(fromAccount)) {
            BrowserUtil.selectByVisibleText(fromEl, fromAccount);
        } else if (!fromOptions.isEmpty()) {
            BrowserUtil.selectByVisibleText(fromEl, fromOptions.get(0));
            fromAccount = BrowserUtil.getSelectedOption(fromEl);
        }

        // TO (must differ from FROM)
        String finalTo = toAccount;
        if (!toOptions.contains(finalTo) || finalTo.equals(fromAccount)) {
            String fromFinal = fromAccount;
            finalTo = toOptions.stream()
                    .filter(opt -> !opt.equals(fromFinal))
                    .findFirst()
                    .orElse(fromAccount);
        }
        BrowserUtil.selectByVisibleText(toEl, finalTo);

        getDriver().findElement(BTN_TRANSFER).click();
    }

    public boolean isSuccessShown() {
        return BrowserUtil.textContains(getDriver(), TITLE, "Transfer Complete!", 12);
    }
}