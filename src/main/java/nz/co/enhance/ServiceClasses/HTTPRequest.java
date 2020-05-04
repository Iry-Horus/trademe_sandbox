package nz.co.enhance.ServiceClasses;

import nz.co.enhance.HelperClasses.TextPrettifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HTTPRequest {

    public String endpointURL;
    public String body;
    public String type;
    public List<List<String>> headers;
    public Request request;
    public String response = null;
    public int responseCode;

    public void sendRequest() {
        response = request.sendRequest();
        responseCode = request.getResponseCode();
    }

    public String logRequestAndResponse() {
        String result;
        result = "\nRequest endpoint: " + type.toUpperCase() + " " + endpointURL + "\n";
        result += "\nRequest headers: " + headers + "\n";
        if (body != null) {
            result += "\nRequest body: " + body + "\n";
        }

        result += "\nResponse code: " + responseCode + "\n";
        if (response.length() > 6000000) {
            result += "\nBody of response was too large to write to the log. Access the response directly if you need the full body.\n";
        } else {
            result += "\nBody of response:\n" + TextPrettifier.indentJson(response) + "\n";
        }
        return result;
    }

    public String getEndpoint() {
        return endpointURL;
    }

    public List<List<String>> getHeaders() {
        return headers;
    }

    //used if we are modifying headers mid-flight
    public void setHeaders(List<List<String>> newHeaders) {
        for (List<String> header : headers) {
            headers.add((new ArrayList<>(Arrays.asList(header.get(0), header.get(0)))));
        }

    }

}
