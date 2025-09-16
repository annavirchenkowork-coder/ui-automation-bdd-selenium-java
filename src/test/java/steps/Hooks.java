package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import util.Driver;

public class Hooks {

    @Before
    public void setUp() {
        // Lazy init happens when you first call getDriver(), but
        // calling once here guarantees a session per scenario if you want that.
        Driver.getDriver();
    }

    @After
    public void tearDown() {
        Driver.closeDriver();
    }
}