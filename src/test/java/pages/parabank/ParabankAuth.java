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
        if (overview.isVisible()) { landOnOverview(); return; }

        if (!login.isVisible()) {
            login.open();
        }

        if (login.isVisible()) {
            login.login(username, password);
            if (overview.isVisible()) { landOnOverview(); return; }
        }

        String err = login.getErrorMessage();
        String url = safeUrl();

        if (err != null && err.toLowerCase().contains("could not be verified")) {
            selfRegisterAndLogin(username, password);
            if (overview.isVisible()) { landOnOverview(); return; }
            throw new IllegalStateException("Registered but still not logged in.");
        }

        if (pageHasInternalError()) {
            getDriver().manage().deleteAllCookies();
            getDriver().navigate().to(BASE + "index.htm");

            login.open();
            if (login.isVisible()) {
                login.login(username, password);
                if (overview.isVisible()) { landOnOverview(); return; }
            }

            // Fallback: register a fresh user
            String freshUser = username + "_" + System.currentTimeMillis();
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) { landOnOverview(); return; }
            throw new IllegalStateException("Could not recover from internal error.");
        }

        if (url.startsWith("data:") || err == null || err.isBlank()) {
            String freshUser = username + "_" + System.currentTimeMillis();
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) { landOnOverview(); return; }
        }

        throw new IllegalStateException("Could not log in to Parabank after self-heal. url=" + url + " err=" + err);
    }

    /** Always land on Accounts Overview so downstream steps can find the accounts table. */
    private static void landOnOverview() {
        BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
        BrowserUtil.waitForVisibility(By.cssSelector("#accountTable tbody tr td a"), 12);
    }

    /** True if the right panel shows the Parabank server error page. */
    private static boolean pageHasInternalError() {
        String panel = BrowserUtil.safeGetText(getDriver(), By.cssSelector("#rightPanel"));
        return panel != null && panel.toLowerCase().contains("internal error");
    }

    private static String safeUrl() {
        try { return getDriver().getCurrentUrl(); } catch (Exception e) { return ""; }
    }

    /**
     * Registers the given username; if we're stuck on register.htm,
     * it will auto-generate new usernames and retry up to a few times. On success we ensure
     * we end up on Accounts Overview.
     */
    private static void selfRegisterAndLogin(String baseUsername, String password) {
        String username = baseUsername;
        final int maxAttempts = 4;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            register.open();
            register.register(username, password);

            if (overview.isVisible()) {
                landOnOverview();
                return;
            }

            if (isLoginPageVisible()) {
                login.login(username, password);
                if (overview.isVisible()) { landOnOverview(); return; }
            }

            String url = safeUrl();
            if (url.contains("/register.htm")) {
                String rightPanel = String.valueOf(
                        BrowserUtil.safeGetText(getDriver(), By.cssSelector("#rightPanel"))
                ).toLowerCase();

                boolean usernameTaken =
                        rightPanel.contains("already") && rightPanel.contains("exist");

                boolean anyValidationError =
                        rightPanel.contains("error") || rightPanel.contains("required");

                if (usernameTaken || anyValidationError || rightPanel.isBlank()) {
                    username = baseUsername + "_" + (System.currentTimeMillis() % 100000);
                    continue;
                }

                BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
                if (overview.isVisible()) { landOnOverview(); return; }
            }

            if (url.endsWith("/index.htm")) {
                if (login.isVisible()) {
                    login.login(username, password);
                    if (overview.isVisible()) { landOnOverview(); return; }
                } else {
                    login.open();
                    if (login.isVisible()) {
                        login.login(username, password);
                        if (overview.isVisible()) { landOnOverview(); return; }
                    }
                }
            }

            // If the site showed internal error, clean and retry with a fresh username.
            if (pageHasInternalError()) {
                getDriver().manage().deleteAllCookies();
                username = baseUsername + "_" + (System.currentTimeMillis() % 100000);
                continue;
            }

            BrowserUtil.sleep(700);
        }

        BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
        if (overview.isVisible()) { landOnOverview(); return; }

        throw new IllegalStateException(
                "Registration finished but neither Overview nor Login is present. URL=" + safeUrl()
        );
    }

    private static boolean isLoginPageVisible() {
        try {
            BrowserUtil.waitForVisibility(By.name("username"), 2);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}