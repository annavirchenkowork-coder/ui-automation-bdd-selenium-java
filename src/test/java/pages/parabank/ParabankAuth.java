package pages.parabank;

import org.openqa.selenium.By;
import util.BrowserUtil;

import static util.Driver.getDriver;

/**
 * Authentication helper for Parabank test flows.
 * Attempts login first, then self-registers and retries when needed.
 * Designed to leave the browser on Accounts Overview for downstream steps.
 */
public class ParabankAuth {
    private static final String BASE = util.ConfigurationReader.getProperty("baseUrl.parabank");

    // Reusable page objects for auth flow
    private static final LoginPage login = new LoginPage();
    private static final AccountsOverviewPage overview = new AccountsOverviewPage();
    private static final RegisterPage register = new RegisterPage();

    /**
     * Ensures a valid session by logging in, or registering then logging in.
     * Uses lightweight recovery paths for transient server errors.
     */
    public static void ensureRegisteredAndLoggedIn(String username, String password) {
        // Already authenticated → normalize landing page
        if (overview.isVisible()) { landOnOverview(); return; }

        // Navigate to login if the form is not visible
        if (!login.isVisible()) {
            login.open();
        }

        // First attempt: direct login
        if (login.isVisible()) {
            login.login(username, password);
            if (overview.isVisible()) { landOnOverview(); return; }
        }

        String err = login.getErrorMessage();
        String url = safeUrl();

        // Credentials rejected → register this user and retry
        if (err != null && err.toLowerCase().contains("could not be verified")) {
            selfRegisterAndLogin(username, password);
            if (overview.isVisible()) { landOnOverview(); return; }
            throw new IllegalStateException("Registered but still not logged in.");
        }

        // Server error page → clear session, retry login, then fallback to fresh registration
        if (pageHasInternalError()) {
            getDriver().manage().deleteAllCookies();
            getDriver().navigate().to(BASE + "index.htm");

            login.open();
            if (login.isVisible()) {
                login.login(username, password);
                if (overview.isVisible()) { landOnOverview(); return; }
            }

            // Fallback: new username to bypass stale state
            String freshUser = username + "_" + System.currentTimeMillis();
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) { landOnOverview(); return; }
            throw new IllegalStateException("Could not recover from internal error.");
        }

        // Ambiguous state (blank error/data URL) → attempt fresh registration
        if (url.startsWith("data:") || err == null || err.isBlank()) {
            String freshUser = username + "_" + System.currentTimeMillis();
            selfRegisterAndLogin(freshUser, password);
            if (overview.isVisible()) { landOnOverview(); return; }
        }

        // Final failure with diagnostics
        throw new IllegalStateException("Could not log in to Parabank after self-heal. url=" + url + " err=" + err);
    }

    /** Normalizes the landing page to Accounts Overview for subsequent steps. */
    private static void landOnOverview() {
        BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
        BrowserUtil.waitForVisibility(By.cssSelector("#accountTable tbody tr td a"), 12);
    }

    /** Detects Parabank internal error page via right panel content. */
    private static boolean pageHasInternalError() {
        String panel = BrowserUtil.safeGetText(getDriver(), By.cssSelector("#rightPanel"));
        return panel != null && panel.toLowerCase().contains("internal error");
    }

    /** Safe current URL getter (handles driver races). */
    private static String safeUrl() {
        try { return getDriver().getCurrentUrl(); } catch (Exception e) { return ""; }
    }

    /**
     * Registers a user and retries login. If registration page loops,
     * auto-suffixes username and continues for a few attempts.
     */
    private static void selfRegisterAndLogin(String baseUsername, String password) {
        String username = baseUsername;
        final int maxAttempts = 4;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            register.open();
            register.register(username, password);

            // Successful registration auto-logs in
            if (overview.isVisible()) {
                landOnOverview();
                return;
            }

            // If we landed on login, try the fresh creds
            if (isLoginPageVisible()) {
                login.login(username, password);
                if (overview.isVisible()) { landOnOverview(); return; }
            }

            String url = safeUrl();
            if (url.contains("/register.htm")) {
                // Inspect right panel for validation hints
                String rightPanel = String.valueOf(
                        BrowserUtil.safeGetText(getDriver(), By.cssSelector("#rightPanel"))
                ).toLowerCase();

                boolean usernameTaken =
                        rightPanel.contains("already") && rightPanel.contains("exist");

                boolean anyValidationError =
                        rightPanel.contains("error") || rightPanel.contains("required");

                // Adjust username on collision or noisy validation states
                if (usernameTaken || anyValidationError || rightPanel.isBlank()) {
                    username = baseUsername + "_" + (System.currentTimeMillis() % 100000);
                    continue;
                }

                // Sometimes registration succeeds but doesn’t redirect; hard-nav to overview
                BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
                if (overview.isVisible()) { landOnOverview(); return; }
            }

            if (url.endsWith("/index.htm")) {
                // Handle both cases: login form already visible or needs opening
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

            // Clear cookies and rotate username on server error loop
            if (pageHasInternalError()) {
                getDriver().manage().deleteAllCookies();
                username = baseUsername + "_" + (System.currentTimeMillis() % 100000);
                continue;
            }

            // Small backoff to avoid hammering transitions
            BrowserUtil.sleep(700);
        }

        // One last attempt to normalize landing state
        BrowserUtil.openPage("baseUrl.parabank", "overview.htm");
        if (overview.isVisible()) { landOnOverview(); return; }

        // Out of retries
        throw new IllegalStateException(
                "Registration finished but neither Overview nor Login is present. URL=" + safeUrl()
        );
    }

    /** Quick probe for the login form without throwing. */
    private static boolean isLoginPageVisible() {
        try {
            BrowserUtil.waitForVisibility(By.name("username"), 2);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}