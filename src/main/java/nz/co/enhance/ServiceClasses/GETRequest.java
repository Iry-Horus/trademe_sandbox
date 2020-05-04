package nz.co.enhance.ServiceClasses;

import java.util.*;

public class GETRequest extends HTTPRequest {
    /**
     * For a GET request:
     * - We always have an endpoint
     * - Sometimes that endpoint has parameters we want to set - set them before passing the endpoint in to this request
     * - We don't have a body
     * - Often we have headers but these can be set to null if none are required.
     */

    public GETRequest(String endpointURL, List<List<String>> headers) {
        this.endpointURL = endpointURL;
        this.headers = headers;
        type = "GET";
        request = new Request(endpointURL, null, headers, type);
    }
}
