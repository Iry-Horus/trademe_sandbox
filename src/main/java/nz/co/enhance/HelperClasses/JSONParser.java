package nz.co.enhance.HelperClasses;

import com.google.gson.JsonArray;
import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

public class JSONParser {
    public JSONObject jsonObj = null;
    public JSONArray jsonArray = null;

    public JSONParser(String JSONToParse) {
        try {
            jsonObj = (JSONObject) JSONValue.parse(JSONToParse); //it's an object
        } catch (Exception e) {
            jsonArray = (JSONArray) JSONValue.parse(JSONToParse); //it's an array
        }
    }

    public JSONParser(JSONObject jsonObject) {
        this.jsonObj = jsonObject;
    }

    public JSONParser(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public String getValue(String value) {
        if (isObject()) {
            return jsonObj.get(value).toString();
        } else {
            return null;
        }
    }

    public JSONParser getChildJSONObject(String childName) {
        if (isObject()) {
            return new JSONParser((JSONObject) jsonObj.get(childName));
        } else {
            return null;
        }
    }

    public JSONParser getChildJSONArray(String childName) {
        if (isObject()) {
            return new JSONParser((JSONArray) jsonObj.get(childName));
        } else {
            return null;
        }
    }

    public List<JSONParser> getJSONArrayAsList(String childName) {
        try {
            List<JSONParser> list = new ArrayList<>();
            JSONArray results = (JSONArray) jsonObj.get(childName);
            for (int i = 0; i < results.size(); i++) {
                JSONParser j = new JSONParser((JSONObject) results.get(i));
                list.add(j);
            }
            return list;
        } catch (Exception e) {
            System.out.println("Node was not a json array.");
            return null;
        }
    }


    public void updateFirstValueOnPath(String node, String value) {
        if (node.contains("/")) {
            List<String> nodeNames = new ArrayList<>();
            for (String nodeName : node.split("/")) {
                nodeNames.add(nodeName);
            }
            jsonObj = getNode(jsonObj, nodeNames, value);
        } else {
            jsonObj.put(node, value);
        }
    }

    public void clearJsonArray(String node) {
        jsonObj.remove(node);
        jsonObj.put(node, new JsonArray());

    }

    public void updateSpecifiedValueOnPath(String node, Object value, int whichToUpdate) {
        if (node.contains("/")) {
            List<String> nodeNames = new ArrayList<>();
            for (String nodeName : node.split("/")) {
                nodeNames.add(nodeName);
            }
            jsonObj = getNode(jsonObj, nodeNames, whichToUpdate, value);
        } else {
            jsonObj.put(node, value);
        }
    }

    public String findFirstValueOnPath(String node) {
        if (node.contains("/")) {
            List<String> nodeNames = new ArrayList<>();
            for (String nodeName : node.split("/")) {
                nodeNames.add(nodeName);
            }
            return (findNodes(nodeNames.get(nodeNames.size() - 2)).get(0).get(nodeNames.get(nodeNames.size() - 1))).toString();
        } else {
            return jsonObj.get(node).toString();
        }
    }

    public String findSpecifiedValueOnPath(String node, int whichNode) {
        if (node.contains("/")) {
            List<String> nodeNames = new ArrayList<>();
            for (String nodeName : node.split("/")) {
                nodeNames.add(nodeName);
            }
            return (findNodes(nodeNames.get(nodeNames.size() - 2)).get(whichNode).get(nodeNames.get(nodeNames.size() - 1))).toString();
        } else {
            return jsonObj.get(node).toString();
        }
    }


    private JSONObject getNode(JSONObject j, List<String> node, Object value) {
        //if there's only one then I update the value
        String thisNode = node.get(0);
        if (j.containsKey(thisNode)) {
            if (node.size() == 1) {
                //update and return
                j.put(thisNode, value);
            } else {
                node.remove(0);
                System.out.println(" the right node!: " + thisNode);

                String type = j.get(thisNode).getClass().toString();
                if (type.contains("JSONArray")) {
                    JSONArray p = (JSONArray) j.get(thisNode);

                    for (int i = 0; i < p.size(); i++) {
                        JSONObject jp = (JSONObject) p.get(i);
                        p.set(i, getNode(jp, node, value));

                    }
                    j.put(thisNode, p);
                } else if (type.contains("JSONObject")) {
                    JSONObject q = (JSONObject) j.get(thisNode);
                    j.put(thisNode, getNode(q, node, value));
                }

            }
        }
        return j;

    }

    private JSONObject getNode(JSONObject j, List<String> node, int nodeNum, Object value) {
        //if there's only one then I update the value
        String thisNode = node.get(0);
        if (j.containsKey(thisNode)) {
            if (node.size() == 1) {
                //update and return
                j.put(thisNode, value);
            } else {
                node.remove(0);
                System.out.println(" the right node!: " + thisNode);

                String type = j.get(thisNode).getClass().toString();
                if (type.contains("JSONArray")) {
                    JSONArray p = (JSONArray) j.get(thisNode);
                    JSONObject jp = (JSONObject) p.get(nodeNum);
                    p.set(nodeNum, getNode(jp, node, value));

                    j.put(thisNode, p);
                } else if (type.contains("JSONObject")) {
                    JSONObject q = (JSONObject) j.get(thisNode);
                    j.put(thisNode, getNode(q, node, value));
                }

            }
        }
        return j;

    }


    private String findValueAnyLevelUniversal(String nodeName) {
        List<HashMap<String, String>> flattenedJSON;
        flattenedJSON = flattenValues(jsonObj);
        for (Map<String, String> keyValuePair : flattenedJSON) {
            if (keyValuePair.containsKey(nodeName)) {
                return keyValuePair.get(nodeName);
            }
        }
        return "";
    }

    public String findValueAnyLevel(String nodeName) {
        if (this.jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                this.jsonObj = (JSONObject) jsonArray.get(i);
                return findValueAnyLevelUniversal(nodeName);
            }
        } else {
            return findValueAnyLevelUniversal(nodeName);
        }
        fail("Node could not be found: " + nodeName);
        return null;
    }


    public static List<HashMap<String, String>> flattenValues(JSONObject values) {
        List<HashMap<String, String>> flatList = new ArrayList<>();

        Object[] valuesCollection = values.values().toArray();
        Object[] keysCollection = values.keySet().toArray();
        for (int i = 0; i < valuesCollection.length; i++) {
            HashMap<String, String> valuePair = new HashMap<>();
            String type;
            try {
                type = valuesCollection[i].getClass().toString();

                if (type.equals("class org.json.simple.JSONArray")) { //array
                    JSONArray ja = (JSONArray) valuesCollection[i];
                    if (ja.size() > 0) { //sometimes there are empty arrays
                        if (ja.get(0).getClass().toString().equals("class java.lang.String")) {
                            //it's an array of strings, badly formattted JSON
                            valuePair.put(keysCollection[i].toString().trim(), valuesCollection[i].toString().trim());
                            flatList.add(valuePair);
                        } else {
                            flatList.addAll(flattenValues((JSONArray) valuesCollection[i]));
                        }
                    }
                } else if (type.equals("class org.json.simple.JSONObject")) { //object
                    flatList.addAll(flattenValues((JSONObject) valuesCollection[i]));
                } else { //flat value
                    valuePair.put(keysCollection[i].toString().trim(), valuesCollection[i].toString().trim());
                    flatList.add(valuePair);
                }
            } catch (Exception e) {
                type = "null";
            }
        }
        return flatList;
    }

    public static List<HashMap<String, String>> flattenValues(JSONArray jsonArray) {
        List<HashMap<String, String>> flatList = new ArrayList<>();
        for (int q = 0; q < jsonArray.size(); q++) {
            flatList.addAll(flattenValues((JSONObject) jsonArray.get(q)));
        }
        return flatList;
    }


    public List<JSONObject> findNodes(String nodeName) {
        List<JSONObject> nodes = new ArrayList<>();

        if (jsonArray != null) { //if we start with an array
            for (int i = 0; i < jsonArray.size(); i++) {
                //if it's an object
                if (jsonArray.get(i).getClass().toString().contains("JSONObject")) {
                    JSONParser jObj = new JSONParser((JSONObject) jsonArray.get(i));
                    nodes.addAll(jObj.findNodesObject(nodeName));
                }
            }
        } else {//we start with an object
            nodes.addAll(findNodesObject(nodeName));
        }
        return nodes;
    }

    private List<JSONObject> findNodesObject(String nodeName) {
        List<JSONObject> nodes = new ArrayList<>();

        Object[] valuesCollection = jsonObj.values().toArray();
        Object[] keysCollection = jsonObj.keySet().toArray();
        //for each element (hashmap) of the object
        for (int q = 0; q < keysCollection.length; q++) {
            //if it's the right node, add it to the list
            try {
                if (keysCollection[q].equals(nodeName)) {
                    //sometimes the nodes have an object, sometimes an array with single object
                    if (valuesCollection[q].getClass().toString().contains("JSONArray")) {
                        JSONArray valueArray = (JSONArray) valuesCollection[q];
                        for (int p = 0; p < valueArray.size(); p++) {
                            nodes.add((JSONObject) valueArray.get(p));
                        }
                    } else if (valuesCollection[q].getClass().toString().contains("JSONObject")) {
                        nodes.add((JSONObject) valuesCollection[q]);
                    }
                } else if (valuesCollection[q].getClass().toString().contains("JSONArray")) {
                    JSONParser j2 = new JSONParser((JSONArray) valuesCollection[q]);
                    //recursively go down the jsonarrays and return nodes
                    nodes.addAll(j2.findNodes(nodeName));
                } else if (valuesCollection[q].getClass().toString().contains("JSONObject")) {
                    JSONParser j2 = new JSONParser((JSONObject) valuesCollection[q]);
                    nodes.addAll(j2.findNodes(nodeName));
                }
            } catch (Exception e) {
                //it was a null
            }
        }
        return nodes;

    }

    private static List<HashMap<String, Object>> flattenObjects(JSONObject values) {
        List<HashMap<String, Object>> flatlist = new ArrayList<>();


        Object[] valuesCollection = values.values().toArray();
        Object[] keysCollection = values.keySet().toArray();
        for (int i = 0; i < valuesCollection.length; i++) {
            String type;
            try {
                type = valuesCollection[i].getClass().toString();
            } catch (Exception e) {
                type = "null";
            }
            //add the original
            HashMap<String, Object> jsonObjectData = new HashMap<>();
            jsonObjectData.put(keysCollection[i].toString(), valuesCollection[i]);
            flatlist.add(jsonObjectData);

            if (type.equals("class org.json.simple.JSONArray")) {  //if it's an array
                JSONArray jsonArray = (JSONArray) valuesCollection[i];
                if (jsonArray.size() > 0) {
                    for (int q = 0; q < jsonArray.size(); q++) {
                        flatlist.addAll(flattenObjects((JSONObject) jsonArray.get(q)));
                    }
                }
            } else if (type.equals("class org.json.simple.JSONObject")) { //if it's an object
                flatlist.addAll(flattenObjects((JSONObject) valuesCollection[i]));
            }
        }
        return flatlist;

    }


    public void writeOutJSONFile(String path) {
        try {
            FileHandler.writeOutFile(jsonObj.toJSONString(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String toJsonString() {
        return jsonObj.toJSONString();
    }


    public JSONParser getAtIndex(int index) {
        if (!isObject()) {
            return new JSONParser((JSONObject) jsonArray.get(index));
        } else {
            return null;
        }
    }


    public JSONObject getObjectAtIndex(int index) {
        if (!isObject()) {
            return ((JSONObject) jsonArray.get(index));
        } else {
            return null;
        }
    }

    public int size() {
        try {
            return jsonArray.size();
        } catch (Exception e) {
            return 0;
        }
    }


    //Retrieves a list of the objects matching the nodename
    public List<Object> findAllObjects(String nodeName) {
        List<Object> values = new ArrayList<>();
        List<HashMap<String, Object>> flattenedJSON = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                flattenedJSON.addAll(flattenObjects(jsonObj));
            }
        } else {
            flattenedJSON.addAll(flattenObjects(jsonObj));
        }

        for (HashMap<String, Object> value : flattenedJSON) {
            if (value.containsKey(nodeName)) {
                values.add(value.get(nodeName));
            }
        }
        return values;
    }

    //Retrieves a list of the string values matching the nodename
    public List<String> findAllValues(String nodeName) {
        List<String> values = new ArrayList<>();
        List<HashMap<String, String>> flattenedJSON;
        flattenedJSON = flattenValues(jsonObj);
        for (Map<String, String> keyValuePair : flattenedJSON) {
            if (keyValuePair.containsKey(nodeName)) {
                values.add(keyValuePair.get(nodeName));
            }
        }
        return values;
    }

    public static boolean isObject(Object objectToTest) {
        try {
            JSONObject jsonObject = (JSONObject) objectToTest;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isObject() {
        if (this.jsonObj != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isArray() {
        if (this.jsonArray != null) {
            return true;
        } else {
            return false;
        }
    }

    public int getCountOfElements(String nodeName) {
        int count = 0;
        List<HashMap<String, Object>> flattenedJSON;
        flattenedJSON = flattenObjects(jsonObj);
        for (Map<String, Object> keyValuePair : flattenedJSON) {
            if (keyValuePair.containsKey(nodeName)) {
                count++;
            }
        }
        return count;
    }

    //takes a comma-delimited list of fields and creates maps for each set
    public List<Map<String, String>> createDataMap(String commaDelimitedFields) {
        List<Map<String, String>> mappedJsonArray = new ArrayList<>();
        String[] fields = commaDelimitedFields.split(",");
        List<List<String>> fieldResults = new ArrayList<>();

        for (String field : fields) {
            fieldResults.add(findAllValues(field));
        }
        for (int i = 0; i < fieldResults.get(0).size(); i++) {
            Map<String, String> newMap = new HashMap<>();
            for (int q = 0; q < fields.length; q++) {
                newMap.put(fields[q], fieldResults.get(q).get(i));
            }
            mappedJsonArray.add(newMap);
        }
        return mappedJsonArray;
    }

    //use jsonPath to retrieve complicated paths
    public String getValueWithJsonPath(String expression) {
        Object value;
        if (jsonObj != null) {
            value = JsonPath.parse(jsonObj).read(expression);
        } else if ((jsonObj == null) && (jsonArray.size() == 1)) {
            value = JsonPath.parse(jsonArray.get(0)).read(expression);
        } else {
            return null;
        }

        if (value.getClass().toString().equalsIgnoreCase("class net.minidev.json.JSONArray")) {
            net.minidev.json.JSONArray Ja = (net.minidev.json.JSONArray) value;
            value = ((net.minidev.json.JSONArray) value).get(0);
        }

        return value.toString();
    }

    //Use this to turn a json array into a map using whatever fieldNames it already uses
    public List<Map<String, String>> createMapFromJsonArray() {
        List<Map<String, String>> mappedArray = new ArrayList<>();


        for (Object obj : jsonArray) {
            JSONObject arrayValue = (JSONObject) obj;
            Map<String, String> map = new HashMap<>();
            String[] keySet = (String[]) arrayValue.keySet().toArray(new String[arrayValue.size()]);
            for (String key : keySet) {
                String value = arrayValue.get(key).toString();
                map.put(key, value);
            }
            mappedArray.add(map);
        }
        return mappedArray;
    }

    public String findArrayValueByKey(String keyName, String keyValue, String fieldtoFind) {
        List<Map<String, String>> mappedArray = createMapFromJsonArray();
        for (Map<String, String> value : mappedArray) {
            if (value.containsKey(keyName)) {
                if (value.get(keyName).equalsIgnoreCase(keyValue)) {
                    if (value.containsKey(fieldtoFind)) {
                        return value.get(fieldtoFind);
                    }
                }
            }

        }
        return null;
    }

}
