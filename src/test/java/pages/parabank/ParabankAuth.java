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
        login.open();
        login.login(username, password);
        if (overview.isVisible()) {
            landOnOverview();
            return;
        }

        String err = login.getErrorMessage();
        String url = getDriver().getCurrentUrl();

        // 2) Credentials not recognized -> register then login
        if (err != null && err.contains("could not be verified")) {
            selfRegisterAndLogin(username, password);
            if (overview.isVisible()) { landOnOverview(); return; }
            throw new IllegalStateException("Registered but still not logged in.");
        }

        // 3) Internal error -> clean session + retry; then fallback to register
        if (err != null && err.contains("internal error")) {
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

        // 4) Unknown state -> try register path as last resort
        if (url.startsWith("data:") || err == null || err.isBlank()) {
            String freshUser = username + "_" + System.currentTimeMillis();
            getDriver().navigate().to(BASE + "register.htm");
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) { landOnOverview(); return; }
        }

        // 5) Give up with diagnostics
        throw new IllegalStateException("Could not log in to Parabank after self-heal. url=" + url + " err=" + err);
    }

    // Helper: always land on Accounts Overview before returning
    private static void landOnOverview() {
        BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
        BrowserUtil.waitForVisibility(By.cssSelector("#accountTable tbody tr td a"), 12);
    }

    private static void selfRegisterAndLogin(String username, String password) {
        login.goToRegister();
        register.registerMinimal(username, password);   // stays logged in on success OR lands on overview
        if (!overview.isVisible()) {
            // Some runs land back on login; do a normal login with the new user
            login.open();
            login.login(username, password);
        }
    }
}