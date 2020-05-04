package nz.co.enhance.StepDefs;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import nz.co.enhance.HelperClasses.HelperMethods;
import nz.co.enhance.HelperClasses.JSONParser;
import nz.co.enhance.ServiceClasses.GETRequest;
import nz.co.enhance.ServiceClasses.HTTPRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class TradeMeSandBoxStepDefs {

    public JSONArray subcategories;
    HTTPRequest request;

    @When("^I send a GET request to retrieve used car makes$")
    public void iSendAGETRequestToRetrieveUsedCarMakes() {
        // Sends a GET Request to the TradeMe SandBox API to retrieve all used car makes.
        String endpoint = "https://api.trademe.co.nz/v1/Categories/UsedCars.json?with_counts=true";
        request = new GETRequest(endpoint, null);
        request.sendRequest();
    }

    @Then("^I assert that the response code is  (\\d+)$")
    public void iAssertThatTheResponseCodeIs(int responseCode) {
        assertTrue("The Response Code returned by the GET Request was not 200.", request.responseCode == responseCode);
    }

    @And("^I print the number of makes returned to the console$")
    public void iPrintTheNumberOfMakesReturnedToTheConsole() {
        // Creates a JSON Object from the JSON returned by the GET Request.
        JSONParser jsonParser = new JSONParser(request.response);
        JSONObject makes = jsonParser.jsonObj;

        // Generates a JSON Array of all listed makes and related data.
        subcategories = (JSONArray) makes.get("Subcategories");

        // Writes the total number of makes.
        Hooks.scenario.write("Number of makes listed including the \"Other\" category: " + subcategories.size());
    }

    @And("^I assert that the brand 'Kia' exists$")
    public void iAssertThatTheBrandKiaExists() {
        // Creates a JSON Object from the JSON returned by the GET Request.
        JSONParser jsonParser = new JSONParser(request.response);
        JSONObject makes = jsonParser.jsonObj;

        // Generates a JSON Array of all listed makes and related data.
        subcategories = (JSONArray) makes.get("Subcategories");

        // Iterates through the JSON Array of makes and looks for "Kia;" sets KIAExists to true if it exists.
        boolean KIAExists = false;
        for (Object subcategory : subcategories) {
            JSONParser jsonSubParser = new JSONParser(subcategory.toString());
            JSONObject make = jsonSubParser.jsonObj;
            if (make.get("Name").equals("Kia")) {
                KIAExists = true;
                break;
            }
        }
        assertTrue("The brand \"Kia\" is not listed as a category.", KIAExists);
    }

    @Then("^I print the current number of Kia cars listed$")
    public void iPrintTheCurrentNumberOfKiaCarsListed() {
        // Creates a JSON Object from the JSON returned by the GET Request.
        JSONParser jsonParser = new JSONParser(request.response);
        JSONObject makes = jsonParser.jsonObj;

        // Generates a JSON Array of all listed makes and related data.
        subcategories = (JSONArray) makes.get("Subcategories");

        // Iterates through the JSON Array of makes and looks for "Kia;" writes the total number of Kia vehicles listed.
        for (Object subcategory : subcategories) {
            JSONParser jsonSubParser = new JSONParser(subcategory.toString());
            JSONObject make = jsonSubParser.jsonObj;
            if (make.get("Name").equals("Kia")) {
                Hooks.scenario.write("Current number of Kia cars listed: " + make.get("Count"));
                break;
            }
        }
    }

    @And("^I assert that the make \"Hispano Suiza\" does not exist$")
    public void iAssertThatTheMakeDoesNotExist() throws Throwable {
        // Creates a JSON Object from the JSON returned by the GET Request.
        JSONParser jsonParser = new JSONParser(request.response);
        JSONObject makes = jsonParser.jsonObj;

        // Generates a JSON Array of all listed makes and related data.
        subcategories = (JSONArray) makes.get("Subcategories");

        // Iterates through the JSON Array of makes and looks for "Hispano Suizo;" sets hispanoSuizoExists to true if found.
        boolean hispanoSuizaExists = false;
        for (Object subcategory : subcategories) {
            JSONParser jsonSubParser = new JSONParser(subcategory.toString());
            JSONObject make = jsonSubParser.jsonObj;
            if (make.get("Name").equals("Hispano Suiza")) {
                hispanoSuizaExists = true;
                break;
            }
        }
        assertFalse("Hispano Suiza is listed as a category",  hispanoSuizaExists);
    }
}
