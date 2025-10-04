package util;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class BrowserUtil {

    private BrowserUtil() {
    }

    private static WebDriver driver() {
        return Driver.getDriver();
    }

    /* ---------------------------
       Navigation / Windows
       --------------------------- */

    /**
     * Switch to a window by exact title; if not found, return to original.
     */
    public static void switchToWindowByTitle(String targetTitle) {
        String origin = driver().getWindowHandle();
        for (String handle : driver().getWindowHandles()) {
            driver().switchTo().window(handle);
            if (targetTitle.equals(driver().getTitle())) return;
        }
        driver().switchTo().window(origin);
    }

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

    /**
     * Hover over an element.
     */
    public static void hover(WebElement element) {
        new Actions(driver()).moveToElement(element).perform();
    }

    /**
     * Double-click an element.
     */
    public static void doubleClick(WebElement element) {
        new Actions(driver()).doubleClick(element).perform();
    }

    /**
     * Scroll element into view (center) and click via JS.
     */
    public static void clickWithJS(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver();
        js.executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    /**
     * Scroll element into view (center).
     */
    public static void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver())
                .executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
    }

    /**
     * Set an attribute via JS.
     */
    public static void setAttribute(WebElement element, String name, String value) {
        ((JavascriptExecutor) driver())
                .executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, name, value);
    }

    /**
     * Set a value via JS.
     */
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

    /**
     * Get trimmed text from elements.
     */
    public static List<String> getElementsText(List<WebElement> elements) {
        List<String> out = new ArrayList<>();
        for (WebElement el : elements) {
            if (el != null) out.add(el.getText().trim());
        }
        return out;
    }

    /**
     * Find by locator and return trimmed texts.
     */
    public static List<String> getElementsText(By locator) {
        return getElementsText(driver().findElements(locator));
    }

    public static void typeAndCommit(WebDriver driver, By locator, String text) {
        WebElement element = driver.findElement(locator);
        String value = (text == null) ? "" : text;

        element.clear();
        element.sendKeys(value);

        // lightweight check (retry a few times)
        for (int i = 0; i < 3; i++) {
            if (value.equals(element.getAttribute("value"))) {
                break; // confirmed
            }
            element.clear();
            element.sendKeys(value);
        }

        // blur/commit
        element.sendKeys(Keys.TAB);
    }

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

    public static String safeGetText(WebDriver driver, By locator) {
        return driver.findElements(locator).isEmpty()
                ? ""
                : driver.findElement(locator).getText().trim();
    }

    /* ---------------------------
       Waits
       --------------------------- */

    /**
     * Hard sleep (use sparingly).
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public static WebElement waitForVisibility(By locator, int timeoutSec) {
        return new WebDriverWait(driver(), Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickability(By locator, int timeoutSec) {
        return new WebDriverWait(driver(), Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait until document.readyState === 'complete'.
     */
    public static void waitForPageToLoad(int timeoutSec) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutSec));
        ExpectedCondition<Boolean> jsLoad = d ->
                "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState"));
        wait.until(jsLoad);
    }

    /**
     * Wait for element reference to recover from staleness.
     */
    public static void waitForStaleRecovery(WebElement element, int timeoutSec) {
        new WebDriverWait(driver(), Duration.ofSeconds(timeoutSec)).until(d -> {
            try {
                element.isEnabled();
                return true;
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    /**
     * Retry a normal Selenium click for up to timeoutSec seconds.
     */
    public static void clickWithRetry(WebElement element, int timeoutSec) {
        long end = System.currentTimeMillis() + timeoutSec * 1000L;
        WebDriverException last = null;
        while (System.currentTimeMillis() < end) {
            try {
                element.click();
                return;
            } catch (WebDriverException e) {
                last = e;
                sleep(500);
            }
        }
        if (last != null) throw last;
    }

    /* ---------------------------
       Presence / Display helpers
       --------------------------- */

    public static boolean isDisplayed(By by) {
        try {
            return driver().findElement(by).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /* ---------------------------
       Select (dropdown) helpers
       --------------------------- */

    public static List<String> getAllSelectOptions(WebElement selectElement) {
        return getElementsText(new Select(selectElement).getOptions());
    }

    public static void selectByVisibleText(WebElement selectElement, String text) {
        new Select(selectElement).selectByVisibleText(text);
    }

    public static String getSelectedOption(WebElement selectElement) {
        return new Select(selectElement).getFirstSelectedOption().getText().trim();
    }
}