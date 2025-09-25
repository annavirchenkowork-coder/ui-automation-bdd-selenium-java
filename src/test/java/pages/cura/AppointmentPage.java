package pages.cura;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import util.BrowserUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AppointmentPage {
    private final WebDriver driver;

    private static final By ROOT           = By.id("appointment");
    private static final By SEL_FACILITY   = By.id("combo_facility");
    private static final By CHK_READMIT    = By.id("chk_hospotal_readmission");
    private static final By RDO_MEDICARE   = By.id("radio_program_medicare");
    private static final By RDO_MEDICAID   = By.id("radio_program_medicaid");
    private static final By RDO_NONE       = By.id("radio_program_none");
    private static final By TXT_VISIT_DATE = By.id("txt_visit_date");           // expects dd/MM/yyyy
    private static final By TXT_COMMENT    = By.id("txt_comment");
    private static final By BTN_BOOK       = By.id("btn-book-appointment");

    public AppointmentPage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForVisible() {
        BrowserUtil.waitForVisibility(ROOT, 8);
    }

    public boolean isVisible() {
        return BrowserUtil.isDisplayed(ROOT);
    }

    public void selectFacility(String visibleText) {
        new Select(driver.findElement(SEL_FACILITY)).selectByVisibleText(visibleText);
    }

    public void setReadmission(boolean checked) {
        WebElement box = driver.findElement(CHK_READMIT);
        if (box.isSelected() != checked) box.click();
    }

    public void chooseProgram(String program) {
        String p = program == null ? "" : program.trim().toLowerCase();
        By loc = switch (p) {
            case "medicare" -> RDO_MEDICARE;
            case "medicaid" -> RDO_MEDICAID;
            default -> RDO_NONE;
        };
        BrowserUtil.waitForClickability(loc, 8).click();
    }

    /** Accepts YYYY-MM-DD and types dd/MM/yyyy as required by the UI. */
    public void setVisitDateFromYmd(String yyyyMmDd) {
        WebElement date = driver.findElement(TXT_VISIT_DATE);
        date.clear();
        if (yyyyMmDd != null && !yyyyMmDd.isBlank()) {
            LocalDate d = LocalDate.parse(yyyyMmDd, DateTimeFormatter.ISO_LOCAL_DATE);
            String ddMMyyyy = d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            date.sendKeys(ddMMyyyy);
        }
    }

    public void setComment(String comment) {
        driver.findElement(TXT_COMMENT).clear();
        driver.findElement(TXT_COMMENT).sendKeys(comment == null ? "" : comment);
    }

    public void submit() {
        BrowserUtil.waitForClickability(BTN_BOOK, 8).click();
    }

    /** Convenience for the datatable-driven step. */
    public void fillFrom(Map<String,String> data) {
        selectFacility(data.get("facility"));
        setReadmission(parseBool(data.get("applyReadmission")));
        chooseProgram(data.get("program"));
        setVisitDateFromYmd(data.get("visitDate"));
        setComment(data.get("comment"));
    }

    private boolean parseBool(String s) {
        return "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s);
    }
}