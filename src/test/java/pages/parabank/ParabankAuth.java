package pages.parabank;

import static util.Driver.getDriver;

public class ParabankAuth {
    private static final String BASE = util.ConfigurationReader.getProperty("baseUrl.parabank");

    private static final LoginPage login = new LoginPage();
    private static final AccountsOverviewPage overview = new AccountsOverviewPage();
    private static final RegisterPage register = new RegisterPage();

    public static void ensureRegisteredAndLoggedIn(String username, String password) {
        // 1) First attempt
        login.open();
        login.login(username, password);
        if (overview.isVisible()) return;

        // Inspect page state
        String err = login.getErrorMessage();
        String url = getDriver().getCurrentUrl();

        // 2) Credentials not recognized -> register then login
        if (err.contains("could not be verified")) {
            selfRegisterAndLogin(username, password);
            if (overview.isVisible()) return;
            throw new IllegalStateException("Registered but still not logged in.");
        }

        // 3) Internal error page -> clean session + retry once; then fallback to register
        if (err.contains("internal error")) {
            getDriver().manage().deleteAllCookies();
            getDriver().navigate().to(BASE + "index.htm");
            login.open();
            login.login(username, password);
            if (overview.isVisible()) return;

            // fallback: new username (backend sometimes loses users)
            String freshUser = username + "_" + System.currentTimeMillis();
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) return;
            throw new IllegalStateException("Could not recover from internal error.");
        }

        // 4) Unknown state (e.g., blank/data:, HTTP 400): try register path as last resort
        if (url.startsWith("data:") || err.isBlank()) {
            getDriver().navigate().to(BASE + "register.htm");
            String freshUser = username + "_" + System.currentTimeMillis();
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) return;
        }

        // 5) Give up with diagnostics
        throw new IllegalStateException("Could not log in to Parabank after self-heal. url=" + url + " err=" + err);
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