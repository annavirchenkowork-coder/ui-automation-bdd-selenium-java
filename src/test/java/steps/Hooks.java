package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.junit.jupiter.api.Assumptions;
import util.Driver;
import util.ParabankHealth;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    @Before
    public void setUp() {
        // Initialize driver before each scenario
        Driver.getDriver();
    }

    @Before("@parabank")
    public void checkParabankHealth() {
        boolean isUp = ParabankHealth.isUp();
        Assumptions.assumeTrue(
                isUp,
                "Skipping test: Parabank is down or showing internal error."
        );
    }

    @After(order = 1)
    public void takeScreenshotOnFailure(Scenario scenario) {
        WebDriver driver = Driver.getDriver();
        if (driver == null) return;

        // Capture screenshot only if the scenario failed
        if (scenario.isFailed()) {
            try {
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

                // Attach screenshot to Cucumber report
                scenario.attach(png, "image/png", scenario.getName());

                // Save screenshot to target/screenshots folder for local review
                String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
                String ts = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS").format(LocalDateTime.now());
                Path out = Paths.get("target", "screenshots", safeName + "_" + ts + ".png");
                Files.createDirectories(out.getParent());
                Files.write(out, png);
            } catch (Throwable ignored) {
                // Ignore any issues with saving screenshots (doesn’t affect tests)
            }
        }
    }

    @After(order = 0)
    public void tearDown() {
        // Always quit driver after each scenario
        Driver.closeDriver();
    }
}