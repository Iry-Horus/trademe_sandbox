package nz.co.enhance.HelperClasses;

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import nz.co.enhance.Automator;
import nz.co.enhance.Element;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;


public class HelperMethods {

    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //pass a list of colon-delimited values
    public static DataTable createDataTableWithHeaders(List<String> listValues) {
        List<List<String>> dataArray = new ArrayList<>();

        int size = listValues.get(0).split(":").length;

        //this is the header row which is intentionally blank.
        List<String> cd0 = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            cd0.add("");
        }
        dataArray.add(cd0);

        for (String values : listValues) {
            List<String> row = new ArrayList<>();
            String[] splitValues = values.split(":");
            for (int i = 0; i < size; i++) {
                row.add(splitValues[i]);
            }
            dataArray.add(row);
        }
        return DataTable.create(dataArray);

    }

    public static DataTable createDataTable(List<String> listValues) {
        List<List<String>> dataArray = new ArrayList<>();

        int size = listValues.get(0).split(":").length;

        for (String values : listValues) {
            List<String> row = new ArrayList<>();
            String[] splitValues = values.split(":");
            for (int i = 0; i < size; i++) {
                row.add(splitValues[i]);
            }
            dataArray.add(row);
        }
        return DataTable.create(dataArray);

    }

    public static Map<String, String> createMap(List<String> values) {
        Map<String, String> dataArray = new HashMap<>();
        for (String pair : values) {
            String[] pairValues = pair.split(":");
            dataArray.put(pairValues[0], pairValues[1]);
        }
        return dataArray;
    }

    //pass a list of  lists
    public static DataTable createDataTableWithHeadersUsingList(List<List<String>> listValues) {
        List<List<String>> dataArray = new ArrayList<>();
        int size = listValues.get(0).size();

        //this is the header row which is intentionally blank.
        List<String> cd0 = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            cd0.add("");
        }
        dataArray.add(cd0);
        for (List<String> values : listValues) {
            dataArray.add(values);
        }
        return DataTable.create(dataArray);
    }

    public static String createRandomString(int lengthOfString) {
        String randomStringComponents = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(lengthOfString);
        for (int i = 0; i < lengthOfString; i++)
            sb.append(randomStringComponents.charAt(rnd.nextInt(randomStringComponents.length())));
        return sb.toString();
    }

    public static String createRandomNumber(int lengthOfString) {
        String randomNumberComponents = "0123456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(lengthOfString);
        for (int i = 0; i < lengthOfString; i++)
            sb.append(randomNumberComponents.charAt(rnd.nextInt(randomNumberComponents.length())));
        return sb.toString();
    }

    public static String createRandomSpecialChars(int lengthOfString) {
        String randomCharsComponents = "!@#$%^&*";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(lengthOfString);
        for (int i = 0; i < lengthOfString; i++)
            sb.append(randomCharsComponents.charAt(rnd.nextInt(randomCharsComponents.length())));
        return sb.toString();
    }

    //Reflections
    public static Set<Class<?>> getClassList(String nameSpace) {

        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(nameSpace))));

        return reflections.getSubTypesOf(Object.class);
    }


    //Returns a new instance of a class by telling it which one you want. Very useful for duel-platform mobile
    public static String getQualifiedClassName(Set<Class<?>> classes, String className) {

        for (Object foundClass : classes.toArray()) {
            if (foundClass.toString().contains(className)) {
                return ((Class) foundClass).getName();
            }
        }
        return "Class not found.";
    }

    //The below are used in situations where you know the name of a field you want to retrieve and you specify
    //it in the step def.


    public static String getFieldFromClassAsString(String fieldName, Class clazz) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.get(clazz).toString();
        } catch (Exception e) {
            System.out.println("Field could not be found in reflection - check field name.");
            return null;
        }
    }

    public static String getFieldFromObjectAsString(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (String) field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static void invokeMethodInClass(String methodName, Class clazz) {
        try {
            Method method = clazz.getMethod(methodName, null);
            method.invoke(clazz.newInstance(), null);
        } catch (Exception e) {
            System.out.println("Method could not be found in reflection - check method name.");
        }
    }


    public static String[] getFieldFromClassAsStringArray(String fieldName, Class clazz) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return (String[]) field.get(clazz);
        } catch (Exception e) {
            System.out.println("Field could not be found in reflection - check field name.");
            return null;
        }
    }

    // Gives you the current date in whatever format you like
    // e.g. Wednesday 14th December 2016 12.07pm
    // ddHHmmss = 14120734
    // dd/MM/YYYY = 14/12/2016
    // YYYYMMdd = 20161214
    // d MMM YYYY = 14 Dec 2016
    // EEEE d MMM YYYY = Wednesday 14 Dec 2016
    // HH:ss EEE d MMM YYYY = 12:07 Tue 12 Dec 2016
    public static String getDateInFormat(String format) {
        return DateHelper.calculateDate(format);
    }

    //Gives you the current date plus or minus whatever number of days specify, using the format you define (see above for
    //examples of format). Accepts negatives to calculate days in the past.
    // eg Wednesday 14th December 2016 12.07pm
    // format: dd/MM/YY, amount: 0 = 14/12/16
    // format: dd/MM/YY, amount: 1 = 15/12/16
    // format: dd/MM/YY, amount: -1 = 13/12/16
    public static String getAlteredDate(String format, int amount) {
        return DateHelper.calculateDate(format, amount);
    }

    public static void assertAllResults(List<TestResult> results, Scenario scenario) {
        boolean allPassed = true;
        System.out.println("Results of comparisons: \n");
        for (TestResult result : results) {
            //add on passed or failed to the message if not already there
            if (!(result.message.toLowerCase().contains("failed")) || !(result.message.toLowerCase().contains("passed"))) {
                if (result.result == false) {
                    result.message = "FAILED:  " + result.message;
                } else {
                    result.message = "PASSED: " + result.message;
                }
            }


            System.out.println(result.message);
            scenario.write(result.message + "\n");
            allPassed = allPassed && result.result;
        }
        assertTrue(allPassed);
    }


    public static void assertAllResultsInMap(List<Map<String, String>> results, Scenario scenario) {
        boolean allPassed = true;
        String messages = "Results of comparisons: \n";
        for (Map<String, String> result : results) {
            if (result.get("result").equalsIgnoreCase("false")) {
                allPassed = false;
                messages += result.get("message") + "\n";
            }
        }
        System.out.println(messages);
        scenario.write(messages);
        assertTrue(allPassed);
    }

    public static String findRegEx(String value, String regex, int whichValue) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return (matcher.group(whichValue));
        }
        return "";
    }

    public static List<List<String>> getElementAttributes(WebElement element) {
        List<List<String>> attributes = new ArrayList<>();
        try {
            JavascriptExecutor executor = (JavascriptExecutor) Automator.driver;
            Map<String, String> attributeMap = (Map<String, String>) executor.executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;", element);
            for (int p = 0; p < attributeMap.size(); p++) {
                List<String> attribute = new ArrayList<>();
                String key = attributeMap.keySet().toArray()[p].toString();
                attribute.add(key);
                attribute.add(attributeMap.get(key));
                attributes.add(attribute);
            }
        } catch (Exception e) {
            //something went wrong
        }
        return attributes;
    }

    public static List<List<String>> getElementAttributes(Element element) {
        return getElementAttributes(element.findElement());
    }

    public static TestResult createAssertEqualsResult(int expected, int actual) {
        return createAssertEqualsResult(String.valueOf(expected), String.valueOf(actual));
    }

    public static TestResult createAssertEqualsResult(String expectedName, int expected, String actualName, int actual) {
        return createAssertEqualsResult(expectedName, String.valueOf(expected), actualName, String.valueOf(actual));
    }

    public static TestResult createAssertEqualsResult(String expected, String actual) {
        TestResult result = new TestResult();
        result.actualValue = actual;
        result.expectedValue = expected;

        result.compareValues();
        if (result.result) {
            result.message = "PASSED: Expected value " + result.expectedValue + " matches actual value  " + result.actualValue + "\n";
        } else {
            result.message = "FAILED:  Expected value " + result.expectedValue + " did not match actual value  " + result.actualValue + "\n";
        }
        return result;
    }

    public static TestResult createAssertEqualsResult(String expectedName, String expected, String actualName, String actual) {
        TestResult result = new TestResult();
        result.actualValue = actual;
        result.expectedValue = expected;
        result.expectedValueName = expectedName;
        result.actualValueName = actualName;

        result.compareValues();
        if (result.result) {
            result.message = "PASSED: Expected  " + result.expectedValueName + " value of " + result.expectedValue + " matched actual " + result.actualValueName + " value  " + result.actualValue + "\n";
        } else {
            result.message = "FAILED:  Expected  " + result.expectedValueName + " value of " + result.expectedValue + " did not match actual " + result.actualValueName + " value  " + result.actualValue + "\n";
        }
        return result;
    }

    public static class TestResult {
        public String message = "";
        public String expectedValueName = "";
        public String expectedValue = "";
        public String actualValue = "";
        public String actualValueName = "";
        public boolean result;

        //build your own
        public TestResult() {

        }

        //build a dummy result with just a message
        public TestResult(String message, boolean result) {
            this.result = result;
            this.message = message;
        }


        public void compareValues() {
            result = expectedValue.equals(actualValue);
        }
    }

    public static String getLocalPath() {
        File f = new File("");
        return f.getAbsolutePath();
    }

}


