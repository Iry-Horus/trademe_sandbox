package nz.co.enhance;

import nz.co.enhance.HelperClasses.HelperMethods;
import nz.co.enhance.HelperClasses.PropertiesHandler;
import nz.co.enhance.ServiceClasses.GETRequest;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Properties;

public class TestDriver {

    public static Automator automator;
    public static Properties globalProperties;
    public static Properties envProperties;
    public static String runTarget;
    public static String env;
    public static ExecutionData executionData = new ExecutionData();


    public TestDriver() {
        setup();
    }

    public void setup() {
        globalProperties = new PropertiesHandler().loadProperties("src/main/resources/global.properties"); //global info
        runTarget = setEnvironmentProperty("runTarget"); //the browser or execution target for the test
        env = setEnvironmentProperty("env").toLowerCase(); //the environment to run in e.g. QA, preprod, prod etc

        //get environment-specific globalProperties
        //envProperties = new PropertiesHandler().loadProperties("src/main/resources/env_" + env + ".globalProperties"); //if using multiple environments

        if (runTarget.equalsIgnoreCase("chrome")) {
            automator = new Automator(AutomationType.CHROME);
            automator.driver.manage().window().maximize();
        }


        if (runTarget.equalsIgnoreCase("headless")) {
            DesiredCapabilities caps = new DesiredCapabilities().chrome();
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            automator = new Automator(AutomationType.CHROME, caps);
        }

        if (runTarget.equalsIgnoreCase("firefox")) {
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("media.gmp-manager.updateEnabled", true);
            profile.setAcceptUntrustedCertificates(true);
            profile.setPreference("media.autoplay.default", 0);
            DesiredCapabilities caps = new DesiredCapabilities();
            FirefoxOptions ffo = new FirefoxOptions();
            ffo.setProfile(profile);
            caps.merge(ffo);

            automator = new Automator(AutomationType.FIREFOX, caps);
            automator.driver.manage().window().maximize();

        }

        //for Docker/Chrome
        if (runTarget.equalsIgnoreCase("chromedocker")) {
            DesiredCapabilities caps = new DesiredCapabilities();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--headless");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1200");

            caps.setCapability(ChromeOptions.CAPABILITY, options);

            automator = new Automator(AutomationType.CHROME, caps);
        }

        if (automator != null) {
            automator.takeFullPageScreenshots = setBooleanEnvironmentProperty("takeFullPageScreenshots");
        }


    }

    public String getVersion() {
        // gets the currently deployed version and writes to the report/console
        GETRequest getVersionFromHTML = new GETRequest(envProperties.getProperty("baseURL"), null);
        getVersionFromHTML.sendRequest();
        String version = "";
        String versionFromHTML = HelperMethods.findRegEx(getVersionFromHTML.response, "\"play\\/version\" content=\"\\d*\\.\\d*\\.\\d*\\.\\d*", 0);
        if (!versionFromHTML.isEmpty() && versionFromHTML != null) {
            version = versionFromHTML.split("=")[1].replace("\"", "");
        }
        System.out.println("[version] " + version);
        return version;
    }


    public String setEnvironmentProperty(String propertyName) {
        if (System.getenv().containsKey(propertyName)) {
            return System.getenv(propertyName);
        } else if (System.getenv().containsKey(propertyName.toUpperCase())) {   //some instances of windows are uppercasing runtarget
            return System.getenv(propertyName.toUpperCase());
        } else {
            return globalProperties.getProperty(propertyName);
        }
    }


    public Boolean setBooleanEnvironmentProperty(String propertyName) {
        if (System.getenv().containsKey(propertyName)) {
            return Boolean.valueOf(System.getenv(propertyName));
        } else {
            return Boolean.valueOf(globalProperties.getProperty(propertyName));
        }
    }

    public void cleanup() {
        //if we need any logging or extra reporting we can pop it here.
        if (automator != null) {
            automator.quit();
        }
    }
}
