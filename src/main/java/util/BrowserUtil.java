package util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static util.Driver.getDriver;

/**
 * Small WebDriver utility layer used by page objects and steps.
 * Centralizes navigation, waits, simple JS actions, and select helpers.
 */
public final class BrowserUtil {

    private BrowserUtil() {
    }

    private static WebDriver driver() {
        return getDriver();
    }

    /* ---------------------------
       Navigation / Windows
       --------------------------- */

    /**
     * Open a page by resolving a base URL key (from config) and appending a path.
     * Validates the base URL and normalizes the trailing slash.
     */
    public static void openPage(String baseUrlKey, String path) {
        String baseUrl = ConfigurationReader.getProperty(baseUrlKey);
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Missing baseUrl for: " + baseUrlKey);
        }
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        driver().get(baseUrl + path);
    }

    /**
     * Simple login helper for pages with username/password/button fields.
     * Keeps page objects lean where bespoke logic isn’t required.
     */
    public static void performLogin(By userField, By passField, By btnLogin,
                                    String username, String password) {
        driver().findElement(userField).clear();
        driver().findElement(userField).sendKeys(username);

        driver().findElement(passField).clear();
        driver().findElement(passField).sendKeys(password);

        driver().findElement(btnLogin).click();
    }

    /* ---------------------------
       Actions & Scrolling
       --------------------------- */

    /** Scrolls element into view and clicks it via JS for stubborn UI controls. */
    public static void clickWithJS(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver();
        js.executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    /** Sets an input’s value via JS and fires input/change events. */
    public static void setValueJS(By locator, String value) {
        WebElement el = driver().findElement(locator);
        String v = (value == null) ? "" : value;
        ((JavascriptExecutor) driver()).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                el, v
        );
    }

    /* ---------------------------
       Text helpers
       --------------------------- */

    /** Returns trimmed text for a list of elements (null-safe). */
    public static List<String> getElementsText(List<WebElement> elements) {
        List<String> out = new ArrayList<>();
        for (WebElement el : elements) {
            if (el != null) out.add(el.getText().trim());
        }
        return out;
    }

    /**
     * Waits until the element’s text contains the expected string.
     * Returns false on timeout instead of throwing.
     */
    public static boolean textContains(WebDriver driver, By locator, String expected, int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(d -> {
                        String actual = safeGetText(driver, locator);
                        return actual.contains(expected);
                    });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Safe text getter: empty string if element is missing. */
    public static String safeGetText(WebDriver driver, By locator) {
        return driver.findElements(locator).isEmpty()
                ? ""
                : driver.findElement(locator).getText().trim();
    }

    /* ---------------------------
       Waits
       --------------------------- */

    /** Hard sleep; only for non-deterministic waits that can’t be polled. */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /** Explicit wait until an element is visible. */
    public static WebElement waitForVisibility(By locator, int timeoutSec) {
        return new WebDriverWait(driver(), Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Explicit wait until an element is clickable. */
    public static WebElement waitForClickability(By locator, int timeoutSec) {
        return new WebDriverWait(driver(), Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Blocks until document.readyState === 'complete'. */
    public static void waitForPageToLoad(int timeoutSec) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutSec));
        ExpectedCondition<Boolean> jsLoad = d ->
                "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState"));
        wait.until(jsLoad);
    }

    /* ---------------------------
       Presence / Display helpers
       --------------------------- */

    /** True if element exists and isDisplayed(), false on not found or stale. */
    public static boolean isDisplayed(By by) {
        try {
            return driver().findElement(by).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /** Lightweight presence probe (no wait). */
    public static boolean isPresent(By locator) {
        try {
            getDriver().findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /* ---------------------------
       Select (dropdown) helpers
       --------------------------- */

    /** Returns visible texts for all options in a <select>. */
    public static List<String> getAllSelectOptions(WebElement selectElement) {
        return getElementsText(new Select(selectElement).getOptions());
    }

    /** Selects an option by visible text. */
    public static void selectByVisibleText(WebElement selectElement, String text) {
        new Select(selectElement).selectByVisibleText(text);
    }

    /** Returns the currently selected option’s visible text. */
    public static String getSelectedOption(WebElement selectElement) {
        return new Select(selectElement).getFirstSelectedOption().getText().trim();
    }
}