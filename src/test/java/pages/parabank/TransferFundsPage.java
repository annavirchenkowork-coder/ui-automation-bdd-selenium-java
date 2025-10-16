package pages.parabank;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static util.Driver.getDriver;

import util.BrowserUtil;

public class TransferFundsPage {

    private static final By AMOUNT = By.id("amount");
    private static final By FROM = By.id("fromAccountId");
    private static final By TO = By.id("toAccountId");

    private static final By BTN_TRANSFER = By.cssSelector("input[type='submit'][value='Transfer']");

    private static final By SUCCESS_TITLE =
            By.xpath("//div[@id='rightPanel']//h1[contains(@class,'title')][contains(.,'Transfer')]");
    private static final By SUCCESS_MESSAGE =
            By.xpath("//div[@id='rightPanel']//*[contains(.,'has been transferred') or contains(.,'was successfully transferred')]");

    private static final By ERROR_TEXT =
            By.xpath("//div[@id='rightPanel']//*[contains(@class,'error') or contains(.,'insufficient funds') or contains(.,'could not be processed') or contains(.,'Error')]");

    public void open() {
        getDriver().get(util.ConfigurationReader.getProperty("baseUrl.parabank") + "transfer.htm");
        BrowserUtil.waitForVisibility(AMOUNT, 8);
    }

    public void transfer(String amount, String fromAccount, String toAccount) {
        // amount
        WebElement amountEl = getDriver().findElement(AMOUNT);
        amountEl.clear();
        amountEl.sendKeys(amount);

        // selects
        WebElement fromEl = getDriver().findElement(FROM);
        WebElement toEl = getDriver().findElement(TO);

        List<String> fromOptions = BrowserUtil.getAllSelectOptions(fromEl);
        List<String> toOptions = BrowserUtil.getAllSelectOptions(toEl);

        if (fromOptions.contains(fromAccount)) {
            BrowserUtil.selectByVisibleText(fromEl, fromAccount);
        } else if (!fromOptions.isEmpty()) {
            BrowserUtil.selectByVisibleText(fromEl, fromOptions.get(0));
            fromAccount = BrowserUtil.getSelectedOption(fromEl);
        }

        String finalTo = toAccount;
        if (!toOptions.contains(finalTo) || finalTo.equals(fromAccount)) {
            String finalFromAccount = fromAccount;
            finalTo = toOptions.stream()
                    .filter(opt -> !opt.equals(finalFromAccount))
                    .findFirst()
                    .orElse(fromAccount);
        }
        BrowserUtil.selectByVisibleText(toEl, finalTo);

        getDriver().findElement(BTN_TRANSFER).click();
    }

    /**
     * Robust success check: returns true if either the "Transfer Complete" title OR
     * the confirmation message shows up within 10s. If an explicit error appears, returns false.
     */
    public boolean isSuccessShown() {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

        return wait.until(driver -> {
            boolean success =
                    driver.findElements(SUCCESS_TITLE).size() > 0 ||
                            driver.findElements(SUCCESS_MESSAGE).size() > 0;

            boolean error = driver.findElements(ERROR_TEXT).size() > 0;

            if (success) return true;
            if (error) return false;

            return null;
        });
    }
}