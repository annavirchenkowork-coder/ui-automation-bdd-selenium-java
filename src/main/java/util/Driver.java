package util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

public class Driver {

    private Driver() {}

    private static final InheritableThreadLocal<WebDriver> driverPool = new InheritableThreadLocal<>();

    public static WebDriver getDriver() {
        if (driverPool.get() == null) {

            String browserName = System.getProperty("browser",
                    ConfigurationReader.getProperty("browser")); // chrome | firefox | remote-chrome | remote-firefox
            boolean headless = Boolean.parseBoolean(System.getProperty("headless",
                    ConfigurationReader.getProperty("headless")));
            boolean maximize = Boolean.parseBoolean(System.getProperty("maximize",
                    ConfigurationReader.getProperty("maximize")));
            long implicitWaitSec = Long.parseLong(System.getProperty("implicitWaitSec",
                    ConfigurationReader.getProperty("implicitWaitSec", "0")));

            switch (browserName) {
                case "remote-chrome" -> {
                    ChromeOptions rc = new ChromeOptions();
                    if (headless) rc.addArguments("--headless=new");
                    driverPool.set(new RemoteWebDriver(gridUrl(), rc));
                }
                case "remote-firefox" -> {
                    FirefoxOptions rf = new FirefoxOptions();
                    if (headless) rf.addArguments("-headless");
                    driverPool.set(new RemoteWebDriver(gridUrl(), rf));
                }
                case "firefox" -> {
                    FirefoxOptions ff = new FirefoxOptions();
                    if (headless) ff.addArguments("-headless");
                    driverPool.set(new FirefoxDriver(ff));
                }
                case "chrome" -> {
                    ChromeOptions ch = new ChromeOptions();
                    if (headless) ch.addArguments("--headless=new");
                    ch.addArguments("--disable-gpu", "--no-sandbox");
                    driverPool.set(new ChromeDriver(ch));
                }
                default -> throw new IllegalArgumentException("Unknown browser: " + browserName);
            }

            if (maximize) {
                try { driverPool.get().manage().window().maximize(); } catch (Exception ignored) {}
            }
            if (implicitWaitSec > 0) {
                driverPool.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSec));
            }
        }
        return driverPool.get();
    }

    public static void closeDriver() {
        if (driverPool.get() != null) {
            try { driverPool.get().quit(); }
            finally { driverPool.remove(); }
        }
    }

    private static java.net.URL gridUrl() {
        String url = System.getProperty("gridUrl",
                ConfigurationReader.getProperty("gridUrl")); // e.g. http://localhost:4444/wd/hub
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Bad gridUrl: " + url, e);
        }
    }
}