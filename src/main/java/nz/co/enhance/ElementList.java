package nz.co.enhance;

import nz.co.enhance.HelperClasses.HelperMethods;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class ElementList {
    public By byLocator = null;

    //used to instantiate a new webelement list but doesn't fetch till accessed, as per Element
    public ElementList(By by) {
        this.byLocator = by;
    }

    public List<Element> getElementList() {
        waitForElementListToExist(20);
        List<WebElement> webElements = Automator.driver.findElements(byLocator);
        List<Element> elements = new ArrayList<>();
        for (WebElement webElement : webElements) {
            elements.add(new Element(webElement));
        }
        return elements;
    }

    public void waitForElementListToExist(int timeout) {
        for (int i = 0; i < timeout; i++) {
            if (Automator.driver.findElements(byLocator).size() > 0) {
                return;
            } else {
                //wait a second
                HelperMethods.sleep(1);
            }
        }
        fail("No elementList could be found using locator " + byLocator + " within " + timeout + " seconds.");
    }

    public void clickOnItemByText(String textToClick) {
        getElementWithText(textToClick).click();
    }

    public void clickOnItemByValue(String valueToClick) {
        getElementWithValue(valueToClick).click();
    }

    public Element getElementWithText(String textToFind) {
        for (Element listItem : getElementList()) {
            if (listItem.getText().toLowerCase().contains(textToFind.toLowerCase())) {
                return listItem;
            }
        }
        fail("Could not find element with text " + textToFind + " from the list of elements with locator " + byLocator);
        return null;
    }

    public Element getElementWithValue(String valueToFind) {
        for (Element listItem : getElementList()) {
            if (listItem.getAttribute("value").toLowerCase().contains(valueToFind.toLowerCase())) {
                return listItem;
            }
        }
        fail("Could not find element with text " + valueToFind + " from the list of elements with locator " + byLocator);
        return null;
    }

    public Element getElementWithAttributeValue(String attributeName, String attributeValue) {
        for (Element listItem : getElementList()) {
            try {
                if (listItem.getAttribute(attributeName).toLowerCase().contains(attributeValue.toLowerCase())) {
                    return listItem;
                }
            } catch (Exception e) {
                //the attribute doesn't exist on this element
            }
        }
        fail("Could not find element with attribute " + attributeName + " with value " + attributeValue + " from the list of elements with locator " + byLocator);
        return null;
    }


    public int size() {
        return getElementList().size();
    }

    public Element getElement(int position) {
        return getElementList().get(position);
    }
}
