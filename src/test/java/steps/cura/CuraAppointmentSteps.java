package steps.cura;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import pages.cura.*;
import util.ConfigurationReader;
import util.Driver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CuraAppointmentSteps {

    private final WebDriver driver = Driver.getDriver();
    private final HomePage home = new HomePage(driver);
    private final LoginPage login = new LoginPage(driver);
    private final AppointmentPage appt = new AppointmentPage(driver);
    private final ConfirmationPage confirm = new ConfirmationPage(driver);

    // ---------- Background ----------

    @Given("the user is on the CURA home page")
    public void on_cura_home_page() {
        home.open(ConfigurationReader.getProperty("baseUrl.cura"));
    }

    @And("the user logs into CURA with username {string} and password {string}")
    public void login_into_cura(String user, String pass) {
        home.clickMakeAppointment();
        login.login(user, pass);
        appt.waitForVisible();
    }

    // ---------- Scenarios ----------

    @When("the user opens the Make Appointment form")
    public void open_make_appointment_form() {
        // After login we land here; this guarantees the form is present
        appt.waitForVisible();
    }

    @When("the user fills the appointment form with:")
    public void fill_form(io.cucumber.datatable.DataTable table) {
        Map<String,String> data = table.asMap(String.class, String.class);
        appt.fillFrom(data);
    }

    @When("the user submits the appointment")
    public void submit_appointment() {
        appt.submit();
    }

    @Then("the confirmation page should show:")
    public void confirmation_should_show(io.cucumber.datatable.DataTable table) {
        confirm.waitForVisible();

        Map<String,String> expected = table.asMap(String.class, String.class);

        // Facility
        Assertions.assertEquals(expected.get("facility"), confirm.facility(), "Facility mismatch");

        // Readmission
        String expectedReadmit = parseBool(expected.get("applyReadmission")) ? "Yes" : "No";
        Assertions.assertEquals(expectedReadmit, confirm.readmit(), "Readmission mismatch");

        // Program
        Assertions.assertEquals(expected.get("program"), confirm.program(), "Program mismatch");

        // Visit date: feature uses YYYY-MM-DD; app shows dd/MM/yyyy
        String expectedDate = toDdMMyyyy(expected.get("visitDate"));
        Assertions.assertEquals(expectedDate, confirm.visitDate(), "Visit date mismatch");

        // Comment
        Assertions.assertEquals(expected.get("comment"), confirm.comment(), "Comment mismatch");
    }

    @Then("the appointment should not be submitted")
    public void appointment_should_not_be_submitted() {
        // Still on the form and no confirmation visible
        Assertions.assertTrue(appt.isVisible(), "Expected to remain on Make Appointment form.");
    }

    @Then("the user should remain on the Make Appointment page")
    public void remain_on_form() {
        Assertions.assertTrue(appt.isVisible(), "Appointment form not visible.");
    }

    // ---------- Helpers ----------

    private boolean parseBool(String s) {
        return "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s);
    }

    private String toDdMMyyyy(String yyyyMmDd) {
        if (yyyyMmDd == null || yyyyMmDd.isBlank()) return "";
        var d = LocalDate.parse(yyyyMmDd, DateTimeFormatter.ISO_LOCAL_DATE);
        return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}