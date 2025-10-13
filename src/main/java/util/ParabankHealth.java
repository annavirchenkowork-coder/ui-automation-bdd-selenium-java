package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParabankHealth {

    public static class Health {
        public final boolean ok;
        public final String reason;
        Health(boolean ok, String reason) { this.ok = ok; this.reason = reason; }
    }

    private static final String DEFAULT_BASE =
            ConfigurationReader.getProperty("baseUrl.parabank"); // e.g. https://parabank.parasoft.com/parabank/;

    public static Health check() {
        String base = DEFAULT_BASE.endsWith("/") ? DEFAULT_BASE : DEFAULT_BASE + "/";
        String[] paths = { "login.htm", "", "index.htm" }; // try a few
        if (isTrue(System.getProperty("parabank.health.force"))
                || isTrue(System.getenv("PARABANK_HEALTH_FORCE"))) {
            return new Health(true, "Forced by flag");
        }

        StringBuilder attemptsLog = new StringBuilder();
        for (int i = 0; i < paths.length; i++) {
            String url = base + paths[i];
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setInstanceFollowRedirects(true);
                    conn.setConnectTimeout(4000);
                    conn.setReadTimeout(6000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) Safari/537.36");
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml");
                    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");

                    int code = conn.getResponseCode();
                    String body = readBody(conn);
                    boolean hasBrand = body.contains("PARA BANK") || body.contains("ParaBank")
                            || body.toLowerCase().contains("customer login");
                    boolean hasInternalError = body.contains("An internal error has occurred");

                    // Accept any <500 if page looks like ParaBank and not the red error banner
                    if (code < 500 && hasBrand && !hasInternalError) {
                        return new Health(true, "OK (" + code + ") on " + paths[i]);
                    }

                    attemptsLog.append("[")
                            .append(paths[i]).append(" try ").append(attempt)
                            .append("] code=").append(code)
                            .append(" brand=").append(hasBrand)
                            .append(" internalError=").append(hasInternalError)
                            .append("; ");
                    Thread.sleep(250L * attempt);

                } catch (Exception e) {
                    attemptsLog.append("[")
                            .append(paths[i]).append(" try ").append(attempt)
                            .append("] ex=").append(e.getClass().getSimpleName())
                            .append(":").append(e.getMessage()).append("; ");
                    try { Thread.sleep(250L * attempt); } catch (InterruptedException ignored) {}
                }
            }
        }
        return new Health(false, attemptsLog.toString());
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