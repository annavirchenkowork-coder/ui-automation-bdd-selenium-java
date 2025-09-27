package pages.cura;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import util.BrowserUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import static util.BrowserUtil.clickWithJS;

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

    private static final DateTimeFormatter ISO_YMD   = DateTimeFormatter.ISO_LOCAL_DATE;      // 2025-10-05
    private static final DateTimeFormatter DMY_SLASH = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DM_SLASH  = DateTimeFormatter.ofPattern("d/M/yyyy");

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

    private LocalDate parseFlexibleDate(String s) {
        for (DateTimeFormatter f : List.of(ISO_YMD, DMY_SLASH, DM_SLASH)) {
            try { return LocalDate.parse(s, f); } catch (DateTimeParseException ignored) {}
        }
        throw new IllegalArgumentException("Unsupported date format: " + s + " (use yyyy-MM-dd or dd/MM/yyyy)");
    }

    /** Accepts YYYY-MM-DD and types dd/MM/yyyy as required by the UI. */
    public void setVisitDate(String dateStr) {
        WebElement date = driver.findElement(TXT_VISIT_DATE);
        date.clear();
        if (dateStr == null || dateStr.isBlank()) return;
        LocalDate d = parseFlexibleDate(dateStr.trim());
        date.sendKeys(d.format(DMY_SLASH));
        // Close the calendar overlay so the next field can receive input
        date.sendKeys(Keys.ESCAPE);
    }

    public void setComment(String comment) {
        BrowserUtil.setValueJS(TXT_COMMENT, comment);
    }

    public void submit() {
        clickWithJS(driver.findElement(BTN_BOOK));
    }

    /** Convenience for the datatable-driven step. */
    public void fillFrom(Map<String,String> data) {
        selectFacility(data.get("facility"));
        setReadmission(parseBool(data.get("applyReadmission")));
        chooseProgram(data.get("program"));
        setVisitDate(data.get("visitDate"));
        setComment(data.get("comment"));
    }

    private boolean parseBool(String s) {
        return "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s);
    }
}