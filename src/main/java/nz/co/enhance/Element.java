package nz.co.enhance;

import nz.co.enhance.HelperClasses.HelperMethods;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class Element {
    public By locator = null;
    public WebElement webElement = null;
    public int defaultSeconds = 20; //default wait to find all elements

    //when instantiating the element we DON'T fetch it. This is so we can set them up without them being visible yet.
    public Element(By locator) {
        this.locator = locator;
    }

    //If we already have a webelement (e.g. we have detached a parent or child webelement from an Element) and we want to wrap it
    public Element(WebElement element) {
        webElement = element;
    }

    //Checks for existence using a fluent wait that can be overridden
    public boolean exists(int fluentWait) {
        if (this.locator == null) { //it's an Element constructed with an existing webElement
            return webElement.isEnabled(); //we know it existed, does it still exist on the page?
        } else {
            for (int i = 0; i < fluentWait; i++) {
                List<WebElement> elements = Automator.driver.findElements(this.locator);
                if (elements.size() > 0) {
                    webElement = elements.get(0);
                    return true;
                } else {
                    HelperMethods.sleep(1);
                }
            }
            return false;
        }
    }

    public boolean exists() {
        return exists(defaultSeconds);
    }

    public WebElement findElement() {
        if (locator == null) {
            return webElement;
        } else {
            if (exists()) {
                return Automator.driver.findElement(locator);
            } else {
                fail("Element does not exist after " + defaultSeconds + " seconds. Locator: " + locator.toString());
                return null;
            }
        }
    }

    public Boolean isDisplayed(int seconds) {
        if (exists(20)) {
            for (int i = 0; i < seconds; i++) {
                if (findElement().isDisplayed()) {
                    return true;
                } else {
                    HelperMethods.sleep(1); //exists but is not visible
                }
            }
        } else {
            fail("Element checked for visibility does not exist and thus is not visible. Locator: " + locator.toString());
        }
        return false;
    }

    public Boolean isDisplayed() {
        return isDisplayed(defaultSeconds);
    }

    public Boolean isEnabled() {
        try {
            return findElement().isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isSelected() {
        try {
            return findElement().isSelected();
        } catch (Exception e) {
            return false;
        }
    }


    //The waitForElementTo.... methods will fail the script if the condition is not true within the timeout.
    //The exists(), isDisplayed() and similar will not fail but return true or false

    public void waitForElementToBeDisplayed(int seconds) {
        if (isDisplayed(seconds)) {
            return;
        } else {
            fail("Element exists but is not visible after " + seconds + " seconds. Locator: " + locator.toString());
        }
    }


    //Waits x seconds for the element to exist - note does not check visibility/isDisplayed, only existence
    public void waitForElementToExist(int seconds) {
        if (exists(seconds)) {
            return;
        } else {
            fail("Element does not exist after " + seconds + " seconds. Locator: " + locator.toString());
        }
    }


    //Waits x seconds for the element's attribute to be the specified value
    public void waitForElementAttribute(int seconds, String attribute, String value) {
        int i = 0;
        while (i < seconds) {
            if (exists(seconds)) {
                if (findElement().getAttribute(attribute).toLowerCase().equals(value.toLowerCase()))
                    return;
            } else {
                HelperMethods.sleep(1);
                i++;
            }
        }
        fail("Element's attribute " + attribute + " was not " + value + " after " + seconds + " seconds. Locator: " + locator.toString());
    }


    //Waits x seconds for the element to not exist
    public void waitForElementNotToExist(int seconds) {
        int i = 0;
        while (i < seconds) {
            if (!exists(1)) {
                return;
            } else {
                HelperMethods.sleep(1);
                i++;
            }
        }
        fail("Element still exists after " + seconds + " seconds. Locator: " + locator.toString());
    }

    //Waits x seconds for the element to NOT be displayed
    public void waitForElementNotToBeDisplayed(int seconds) {
        int i = 0;

        if (this.locator == null) { //if we've made this with an element then we check if it's displayed
            if (!webElement.isDisplayed()) {
                return;
            }
        } else {
            while (i < seconds) {
                if (exists(1)) {
                    if (!isDisplayed(1)) {
                        return;
                    } else {
                        HelperMethods.sleep(1);
                        i++;
                    }
                } else {
                    return; //it doesn't exist so therefore is not displayed
                }
            }
            //we only get to here if the element is still displayed
            fail("Element is still displayed after " + seconds + " seconds. Locator: " + locator.toString());
        }
    }


    public WebElement returnElementWhenExists(int seconds) {
        waitForElementToExist(seconds);
        if (this.webElement != null) {
            return webElement;
        } else {
            return findElement();
        }
    }

    public WebElement returnElementWhenDisplayed(int seconds) {
        waitForElementToBeDisplayed(seconds);
        if (this.webElement != null) {
            return webElement;
        } else {
            return findElement();
        }
    }


    //retrieves child elements by an xpath string
    public List<Element> findChildElements(By locator) {
        List<Element> childElements = new ArrayList<>();
        int numberOfSubElements = findElement().findElements(locator).size();
        if (numberOfSubElements > 0) {
            List<WebElement> childWebElements = findElement().findElements(locator);
            for (int i = 0; i < numberOfSubElements; i++) {
                childElements.add(new Element(childWebElements.get(i)));
            }
        }
        return childElements;
    }


    //wrapper everything here.
    //Using find element each time means we always get a fresh copy, it stops the nasty webelement stale reference errors.
    //This is the major guts of a webdriver library, handling the element and passing commands through

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          Basic commands                                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void click() {
        for (int i = 0; i < 10; i++) {
            try {
                if (findElement().isDisplayed()) {
                    if ((Automator.driver.getClass().toString().toLowerCase().contains("edge"))) {
                        Actions actions = new Actions(Automator.driver);
                        actions.click(findElement()).build().perform();
                    } else if ((Automator.driver.getClass().toString().toLowerCase().contains("safari"))) {
                        forceClick();
                    } else {
                        findElement().click();
                    }

                    i = 10;
                } else if (exists(20)) {//if it exists but web page says it's invisible
                    forceClick();
                    i = 10;
                }
            } catch (StaleElementReferenceException s) {
                HelperMethods.sleep(1);
            }
        }
    }

    public void clickAction() {
        Actions actions = new Actions(Automator.driver);
        for (int i = 0; i < 10; i++) {
            try {
                if (findElement().isDisplayed()) {
                    actions.moveToElement(findElement()).click().build().perform();
                    i = 10;
                }
            } catch (StaleElementReferenceException s) {
                HelperMethods.sleep(1);
            }
        }
    }

    public void clickActionByOffset(int x, int y) {
        Actions actions = new Actions(Automator.driver);
        for (int i = 0; i < 10; i++) {
            try {
                if (findElement().isDisplayed()) {
                    actions.moveToElement(findElement(), x, y).click().build().perform();
                    i = 10;
                }
            } catch (StaleElementReferenceException s) {
                HelperMethods.sleep(1);
            }
        }
    }

    public void fastClick() {
        findElement().click();
    }

    //to help mobile testers use correct terminology
    public void tap() { //does a click but named for mobile
        fastClick();
    }

    public void forceClick() {
        //useful for when an element is "invisible" due to parent css but is actually interactable
        JavascriptExecutor executor = (JavascriptExecutor) Automator.driver;
        executor.executeScript("arguments[0].click();", findElement());
    }

    public void urlClick() {
        //Safari-specific
        this.findChild(By.cssSelector("a")).click();
    }

    public String getText() {
        if ((Automator.driver.getClass().toString().toLowerCase().contains("safari"))) {
            return StringUtils.normalizeSpace(findElement().getText());
        } else {
            return findElement().getText();
        }
    }

    public String getValue() {
        return findElement().getAttribute("value");
    }

    public void sendKeys(String keys) {
        findElement().sendKeys(keys);
    }

    public void sendKeys(CharSequence character) {
        findElement().sendKeys(character);
    }

    public void clear() {
        findElement().clear();
    }

    public String getAttribute(String attributeName) {
        return findElement().getAttribute(attributeName);
    }

    public Point getLocation() {
        return findElement().getLocation();
    }

    public List<Element> findChildren(By by) {
        List<Element> elementList = new ArrayList<>();
        for (WebElement element : findElement().findElements(by)) {
            elementList.add(new Element(element));
        }
        return elementList;
    }

    public Element findChild(By by) {
        return new Element(findElement().findElement(by));
    }

    public Element getParent() {
        return new Element(findElement().findElement(By.xpath("..")));
    }

    public void selectDropDownByVisibleText(String strText) {
        Select select = new Select(findElement());
        select.selectByVisibleText(strText);
    }

    public void selectDropDownByValue(String strText) {
        Select select = new Select(findElement());
        select.selectByValue(strText);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                            Swipes/Scrolls/Touches                                                  //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void scrollToElement() {
        Point hoverItem = findElement().getLocation();
        ((JavascriptExecutor) Automator.driver).executeScript("window.scrollBy(0," + (hoverItem.getY() - 200) + ");");
    }

    public void scrollToElement(int offset) {
        Point hoverItem = findElement().getLocation();
        ((JavascriptExecutor) Automator.driver).executeScript("window.scrollBy(0," + (hoverItem.getY() - offset) + ");");
    }

    public void hoverOver() {
        WebElement element = findElement();
        String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
        ((JavascriptExecutor) Automator.driver).executeScript(mouseOverScript, element);
    }

    public void hoverOverAction() {
        Actions actions = new Actions(Automator.driver);
        Actions moveto = actions.moveToElement(findElement());
        moveto.build().perform();
    }

    public void movingHover() {
        Actions actions = new Actions(Automator.driver);
        Actions moveto = actions.moveToElement(findElement()).moveToElement(findElement(), 2, 2);
        moveto.build().perform();
    }

    public void hoverOut() {
        if (Automator.driver.getClass().toString().toLowerCase().contains("safari")) {
            WebElement element = Automator.driver.findElement(By.xpath("//title"));
            String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
            ((JavascriptExecutor) Automator.driver).executeScript(mouseOverScript, element);
        } else {
            WebElement element = Automator.driver.findElement(By.xpath("//title"));
            Actions actions = new Actions(Automator.driver);
            Actions moveto = actions.moveToElement(element);
            moveto.perform();
        }

    }
}
