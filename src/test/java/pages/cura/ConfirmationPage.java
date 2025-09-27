package pages.cura;

import org.openqa.selenium.*;

import static util.BrowserUtil.safeGetText;
import static util.BrowserUtil.waitForVisibility;


public class ConfirmationPage {
    private final WebDriver driver;

    private static final By ROOT       = By.id("summary");
    private static final By FACILITY   = By.id("facility");
    private static final By READMIT    = By.id("hospital_readmission");
    private static final By PROGRAM    = By.id("program");
    private static final By VISIT_DATE = By.id("visit_date");           // dd/MM/yyyy
    private static final By COMMENT    = By.id("comment");

    public ConfirmationPage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForVisible() {
        waitForVisibility(ROOT, 8);
    }

    public String facility()   { return safeGetText(driver, FACILITY); }
    public String readmit()    { return safeGetText(driver, READMIT); }
    public String program()    { return safeGetText(driver, PROGRAM); }
    public String visitDate()  { return safeGetText(driver, VISIT_DATE); }
    public String comment()    { return safeGetText(driver, COMMENT); }
}