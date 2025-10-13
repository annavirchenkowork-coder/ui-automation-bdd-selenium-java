package pages.parabank;

import pages.parabank.LoginPage;
import pages.parabank.RegisterPage;

public final class ParabankAuth {
    private ParabankAuth() {}

    /**
     * Try login; if invalid-cred error appears, register the user and login again.
     */
    public static void ensureLoggedIn(String username, String password) {
        LoginPage login = new LoginPage();
        login.open();
        login.login(username, password);

        if (login.invalidCredsShown()) {
            // self-heal by registering the account
            RegisterPage reg = new RegisterPage();
            reg.open();
            reg.register(username, password);

            // back to login
            login.open();
            login.login(username, password);
        }

        if (!login.loggedIn()) {
            throw new IllegalStateException("Could not log in to Parabank after self-heal.");
        }
    }
}