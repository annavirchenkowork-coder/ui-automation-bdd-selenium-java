package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;

public class ParabankHealth {

    public static class Health {
        public final boolean ok;
        public final String reason;
        Health(boolean ok, String reason) { this.ok = ok; this.reason = reason; }
    }

    private static final String DEFAULT_BASE =
            ConfigurationReader.getProperty("baseUrl.parabank"); // e.g. https://parabank.parasoft.com/parabank/
    private static final String PATH = "login.htm";

    public static Health check() {
        String base = DEFAULT_BASE.endsWith("/") ? DEFAULT_BASE : DEFAULT_BASE + "/";
        String url = base + PATH;

        // quick override: -Dparabank.health.force=true  (or env PARABANK_HEALTH_FORCE=true)
        if (isTrue(System.getProperty("parabank.health.force"))
                || isTrue(System.getenv("PARABANK_HEALTH_FORCE"))) {
            return new Health(true, "Forced by flag");
        }

        int attempts = 3;
        for (int i = 1; i <= attempts; i++) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setInstanceFollowRedirects(true);
                conn.setConnectTimeout((int) Duration.ofSeconds(4).toMillis());
                conn.setReadTimeout((int) Duration.ofSeconds(6).toMillis());
                conn.setRequestProperty("User-Agent", "UI-Auto-Health/1.0");

                int code = conn.getResponseCode();
                String body = readBody(conn);

                // Positive signal: login form marker
                boolean hasLogin = body.toLowerCase(Locale.ROOT).contains("customer login");

                // Negative signal: well-known red error
                boolean hasInternalError = body.contains("An internal error has occurred");

                if (code >= 200 && code < 400 && hasLogin && !hasInternalError) {
                    return new Health(true, "OK (" + code + ")");
                }

                String reason = "HTTP " + code +
                        ", loginMarker=" + hasLogin +
                        ", internalError=" + hasInternalError;
                // brief backoff before next attempt
                Thread.sleep(400L * i);
                if (i == attempts) return new Health(false, reason);

            } catch (Exception e) {
                String reason = "Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage();
                try { Thread.sleep(300L * i); } catch (InterruptedException ignored) {}
                if (i == attempts) return new Health(false, reason);
            }
        }
        return new Health(false, "Unknown");
    }

    private static String readBody(HttpURLConnection conn) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        (conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream())
                ))) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[2048];
            int n;
            while ((n = br.read(buf)) > 0 && sb.length() < 100_000) {
                sb.append(buf, 0, n);
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static boolean isTrue(String v) {
        return v != null && (v.equalsIgnoreCase("true") || v.equals("1") || v.equalsIgnoreCase("yes"));
    }
}