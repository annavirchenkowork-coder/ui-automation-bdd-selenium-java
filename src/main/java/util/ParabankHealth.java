package util;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;

public final class ParabankHealth {
    private ParabankHealth() {}

    public static boolean isUp() {
        String base = ConfigurationReader.getProperty("baseUrl.parabank");
        String url  = base.endsWith("/") ? base + "login.htm" : base + "/login.htm";
        try {
            HttpClient http = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

            // Parabank often returns 200 with an inline "Error!" message; treat both as down.
            String body = res.body() == null ? "" : res.body();
            boolean showsInlineError = body.contains("An internal error has occurred and has been logged.");
            return res.statusCode() == 200 && !showsInlineError;
        } catch (Exception ignored) {
            return false;
        }
    }
}