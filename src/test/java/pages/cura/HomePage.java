package pages.cura;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.BrowserUtil;

public class HomePage {
    private final WebDriver driver;
    private static final By BTN_MAKE_APPT = By.id("btn-make-appointment");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl);
        BrowserUtil.waitForClickability(BTN_MAKE_APPT, 8);
    }

    public void clickMakeAppointment() {
        BrowserUtil.waitForClickability(BTN_MAKE_APPT, 8).click();
    }
}