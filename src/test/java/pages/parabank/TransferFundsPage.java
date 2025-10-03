package pages.parabank;

import org.openqa.selenium.By;

public class TransferFundsPage {
    private static final By AMOUNT = By.id("amount");
    private static final By FROM = By.id("fromAccountId");
    private static final By TO = By.id("toAccountId");
    private static final By BTN_TRANSFER = By.cssSelector("input.button");
    private static final By SUCCESS_TITLE = By.cssSelector("#rightPanel .title");

}