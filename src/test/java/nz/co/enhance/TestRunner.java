package nz.co.enhance;
/**
 * This class controls the Cucumber setup and tags.
 */

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
        //This specifies the type and location of your desired reports.
        plugin = {"pretty", "nz.co.enhance.ReportingClasses.HTML:target/cucumberHTML", "json:target/cucumber.json", "junit:target/cucumber-junit.xml"},
        //This is the location of your Features folder
        features = {"src/test/java/nz/co/tvnz/Features"},
        //This is the location of your StepDefs folder
        glue = {"nz.co.tvnz.StepDefs"},
        //This is an optional flag that lets all tests that match the tags be checked without executing them. Use it to
        //dryRun = true,
        //These are the tags that will be executed if you run this class.
        tags = {"@smoke"})


public class TestRunner {

}
