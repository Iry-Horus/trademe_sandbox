package nz.co.enhance.StepDefs;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import nz.co.enhance.HelperClasses.HelperMethods;
import nz.co.enhance.TestDriver;

public class Hooks {

    public static TestDriver testDriver;
    public static Scenario scenario;

    @Before
    public void before(Scenario scenario) throws Throwable {
        this.scenario = scenario; //we do this so we can write pictures and text to the selenium logs easily
        //instantiate a test driver
        testDriver = new TestDriver();
    }

    @After
    public void after() throws Throwable {
        //call quit on the TestDriver here and do any required logging.
        if (testDriver.automator != null) {
            try {
                if ((scenario.getStatus().equalsIgnoreCase("failed")) || (TestDriver.globalProperties.getProperty("screenshotAll").equalsIgnoreCase("true"))) {
                    testDriver.automator.takeScreenshot(scenario);
                }
                scenario.write("Timestamp at finish: " + HelperMethods.getDateInFormat("HH:mm:ss.SSS"));
                scenario.write("Environment: " + TestDriver.env);
            } catch (Exception e) {
                //Something went wrong
                System.out.println("After hook did not execute properly. Possibly trying to write to a log when there is no log.");
            }
            testDriver.cleanup();
        }
    }
}
