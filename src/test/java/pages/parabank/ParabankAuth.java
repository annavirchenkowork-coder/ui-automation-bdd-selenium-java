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
        // 1) First attempt: normal login
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

        // 2) Credentials not recognized -> register path
        if (err.contains("could not be verified")) {
            selfRegisterAndLogin(username, password);
            if (overview.isVisible()) { landOnOverview(); return; }
            throw new IllegalStateException("Registered but still not logged in.");
        }

        // 3) Internal error -> clean session + retry once, then register fresh user
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

        // 4) Unknown / blank state (blank page, data: URL, no error message) -> try register
        if (url.startsWith("data:") || err.isBlank()) {
            String freshUser = username + "_" + System.currentTimeMillis();
            getDriver().navigate().to(BASE + "register.htm");
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) { landOnOverview(); return; }
        }

        // 5) Give up with diagnostics
        throw new IllegalStateException("Could not log in to Parabank after self-heal. url=" + url + " err=" + err);
    }

    private static void landOnOverview() {
        BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
        BrowserUtil.waitForVisibility(By.cssSelector("#accountTable tbody tr td a"), 12);
    }

    /**
     * Registers a user and ensures we are logged in.
     */
    private static void selfRegisterAndLogin(String username, String password) {
        // Go directly to Register
        register.open();
        register.registerMinimal(username, password);

        // Small settle wait: the site can be slow to redirect/render
        BrowserUtil.sleep(500);

        if (overview.isVisible()) {
            landOnOverview();
            return;
        }

        boolean onLoginForm = !getDriver().findElements(By.name("username")).isEmpty();
        if (onLoginForm) {
            login.open();
            login.login(username, password);
            if (overview.isVisible()) {
                landOnOverview();
                return;
            }
            throw new IllegalStateException("Registered, saw login form, but still not logged in.");
        }

        // Neither overview nor login form → surface what page we're on
        throw new IllegalStateException(
                "Registration finished but neither Overview nor Login is present. URL=" + getDriver().getCurrentUrl());
    }
}