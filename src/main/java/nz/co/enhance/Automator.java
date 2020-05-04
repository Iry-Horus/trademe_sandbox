package nz.co.enhance;

import cucumber.api.Scenario;
import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;
import nz.co.enhance.HelperClasses.FullPageScreenshot;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Automator {
    public static WebDriver driver;
    public DesiredCapabilities caps = null;
    public String URL = null;
    public String automationPlatform = "";
    public Boolean takeFullPageScreenshots = false; //set to true for debugging purposes

    //uses default settings
    public Automator(AutomationType automationType) {
        setupDriver(automationType);
    }

    //Specify your own caps to use - all emulators/devices really should do this and not use the defaults
    public Automator(AutomationType automationType, DesiredCapabilities caps) {
        this.caps = caps;
        setupDriver(automationType);
    }

    //Non-standard remote driver instantiation eg Saucelabs connection, non-standard appium
    public Automator(DesiredCapabilities caps, String URL) {
        this.caps = caps;
        this.URL = URL;
        setupRemoteWebDriver();
    }


    //switch for basic setup
    private void setupDriver(AutomationType automationType) {

        switch (automationType) {
            case FIREFOX:
                setupFirefox();
                automationPlatform = "web";
                break;
            case FIREFOXSELFMANAGED:
                setupFirefoxSelfManaged();
                automationPlatform = "web";
                break;
            case CHROME:
                setupChrome();
                automationPlatform = "web";
                break;
            case CHROMESELFMANAGED:
                setupChromeSelfManaged();
                automationPlatform = "web";
                break;
            default:

        }
    }


    private void setupFirefox() {
        WebDriverManager.getInstance(DriverManagerType.FIREFOX).setup();
        setupFirefoxSelfManaged();
    }

    private void setupFirefoxSelfManaged() {
        if (caps == null) {
            caps = new DesiredCapabilities().firefox();
        }
        driver = new FirefoxDriver(caps);
    }

    private void setupChrome() {
        WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
        setupChromeSelfManaged();
    }

    private void setupChromeSelfManaged() {
        if (caps == null) {
            caps = new DesiredCapabilities().chrome();
        }
        driver = new ChromeDriver(caps);
    }


    private void setupRemoteWebDriver() {
        try {
            automationPlatform = "remote";
            driver = new RemoteWebDriver(new URL(this.URL), caps);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


    public void quit() {
        driver.quit();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                        Webdriver-level stuff                                                   //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void takeScreenshot(Scenario scenario) {
        if (takeFullPageScreenshots) {
            takeFullPageScreenshot(scenario);
        } else {
            takeNormalScreenshot(scenario);
        }
    }

    public void takeNormalScreenshot(Scenario scenario) {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        scenario.embed(screenshot, "image/png");
    }

    //FA 8/4/19 full page screenshotting solution for debugging purposes
    public void takeFullPageScreenshot(Scenario scenario) {
        FullPageScreenshot fullPageScreenshot = new FullPageScreenshot();
        fullPageScreenshot.capturePageScreenshot(driver, scenario);
    }

    public void switchToDefaultTab() {
        List<String> browserTabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(0));
    }

    public void switchToTab(int index) {
        List<String> browserTabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(index));
    }

    public void closeTab(int index) {
        List<String> browserTabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(index));
        driver.close();
        switchToDefaultTab();
    }

    public void openTab() {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        js.executeScript("window.open()");
    }

    public String getCurrentPageUrl() {
        return driver.getCurrentUrl();
    }

    public void scrollToBottomOfPage() {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public void scrollToTopOfPage() {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        js.executeScript("window.scrollTo(0, 0)");
    }

    public WebElement getFullscreenElement() {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        return (WebElement) js.executeScript("var element = document.fullscreenElement; return element");
    }

    public String getCookies() {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        return (String) js.executeScript("return document.cookie");
    }

    public void scrollTo(WebElement element) {
        if (automationPlatform.equals("web")) {
            Point hoverItem = element.getLocation();
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0," + (hoverItem.getY() - 400) + ");");
        } else if (automationPlatform.contains("mobile")) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        }
    }

    public void switchToDefaultFrame() {
        driver.switchTo().defaultContent();
    }

    public void switchToFrame(By by) {
        try {
            driver.switchTo().frame(driver.findElement(by));
        } catch (Exception e) {
            //already on it;
        }
    }

    public final double getWidth() {
        return driver.manage().window().getSize().getWidth();
    }

    public final double getHeight() {
        return driver.manage().window().getSize().getHeight();
    }

    public final int getHeightAsInt() {
        return driver.manage().window().getSize().getHeight();
    }
}
