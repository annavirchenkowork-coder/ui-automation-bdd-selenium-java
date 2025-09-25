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
     * Briefly highlight an element (helps when debugging locally).
     */
    public static void highlight(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver();
        String original = element.getAttribute("style");
        try {
            js.executeScript(
                    "arguments[0].setAttribute('style', (arguments[1]||'') + 'background: yellow; border: 2px solid red;');",
                    element, original);
            sleep(400);
        } finally {
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, original);
        }
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

    public static String safeGetText(WebDriver driver, By locator) {
        return driver.findElements(locator).isEmpty()
                ? ""
                : driver.findElement(locator).getText().trim();
    }
}