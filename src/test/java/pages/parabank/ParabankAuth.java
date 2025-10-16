package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;

import static util.Driver.getDriver;

public class ParabankAuth {
    private static final String BASE = util.ConfigurationReader.getProperty("baseUrl.parabank");

    private static final LoginPage login = new LoginPage();
    private static final AccountsOverviewPage overview = new AccountsOverviewPage();
    private static final RegisterPage register = new RegisterPage();

    public static void ensureRegisteredAndLoggedIn(String username, String password) {
        // First attempt: normal login
        login.open();
        login.login(username, password);
        if (overview.isVisible()) {
            landOnOverview();
            return;
        }

        // Diagnose current state
        String err = login.getErrorMessage();
        err = (err == null) ? "" : err.toLowerCase();
        String url = getDriver().getCurrentUrl();

        // 1) Credentials not recognized -> register then login (if needed)
        if (err.contains("could not be verified")) {
            selfRegisterAndLogin(username, password);
            if (overview.isVisible()) { landOnOverview(); return; }
            throw new IllegalStateException("Registered but still not logged in.");
        }

        // 2) Internal error page -> clean session + retry; then fallback to register
        if (err.contains("internal error")) {
            getDriver().manage().deleteAllCookies();
            getDriver().navigate().to(BASE + "index.htm");
            login.open();
            login.login(username, password);
            if (overview.isVisible()) { landOnOverview(); return; }

            String freshUser = username + "_" + System.currentTimeMillis();
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) { landOnOverview(); return; }
            throw new IllegalStateException("Could not recover from internal error.");
        }

        // 3) Unknown/blank state (e.g., blank page, data:, no error) -> try fresh register
        if (url.startsWith("data:") || err.isBlank()) {
            String freshUser = username + "_" + System.currentTimeMillis();
            getDriver().navigate().to(BASE + "register.htm");
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) { landOnOverview(); return; }
        }

        // 4) Give up with diagnostics
        throw new IllegalStateException("Could not log in to Parabank after self-heal. url=" + url + " err=" + err);
    }

    // Helper: always land on Accounts Overview before returning
    private static void landOnOverview() {
        BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
        BrowserUtil.waitForVisibility(By.cssSelector("#accountTable tbody tr td a"), 12);
    }

    /**
     * Register a user and ensure we end up logged in.
     */
    private static void selfRegisterAndLogin(String username, String password) {
        // Open Register and create the account
        register.open();
        register.registerMinimal(username, password);

        if (overview.isVisible()) {
            landOnOverview();
            return;
        }

        login.open();
        login.login(username, password);
        if (overview.isVisible()) {
            landOnOverview();
            return;
        }

        throw new IllegalStateException(
                "Registered but not logged in; current URL=" + getDriver().getCurrentUrl());
    }
}